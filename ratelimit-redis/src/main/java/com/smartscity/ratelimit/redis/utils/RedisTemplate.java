package com.smartscity.ratelimit.redis.utils;


import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class RedisTemplate {

    private final StatefulRedisConnection<String, String> connection;
    private final String scriptUri;
    private volatile String shaInstance;

    public RedisTemplate(StatefulRedisConnection<String, String> connection) {
        requireNonNull(connection);
        this.connection = connection;
        this.scriptUri = null;
    }


    public RedisTemplate(StatefulRedisConnection<String, String> connection, String scriptUri) {
        this(connection, scriptUri, false);
    }

    public RedisTemplate(StatefulRedisConnection<String, String> connection, String scriptUri, boolean eagerLoad) {
        requireNonNull(connection);
        this.connection = connection;
        this.scriptUri = requireNonNull(scriptUri);
        if (eagerLoad) {
            scriptSha();
        }
    }

    public String scriptSha() {
        // safe local double-checked locking
        // http://shipilev.net/blog/2014/safe-public-construction/
        String sha = shaInstance;
        if (sha == null) {
            synchronized (this) {
                sha = shaInstance;
                if (sha == null) {
                    sha = loadScript();
                    shaInstance = sha;
                }
            }
        }
        return sha;
    }

    private String loadScript() {
        String script;
        try {
            script = readScriptFile();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load Redis LUA script file", e);
        }

        return connection.sync().scriptLoad(script);
    }

    private String readScriptFile() throws IOException {
        URL url = RedisTemplate.class.getClassLoader().getResource(scriptUri);

        if (url == null) {
            throw new IllegalArgumentException("script '" + scriptUri + "' not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }


    public void set(final String key, final String field, final String value){
        RedisCommands<String, String> syncCon = connection.sync();
        syncCon.hset(key, field, value);
    }

    public String get(final String key, final String field){
        RedisCommands<String, String> syncCon = connection.sync();
        return syncCon.hget(key, field);
    }

}
