package com.smartscity.ratelimit.redis.request;


import com.smartscity.ratelimit.redis.utils.RedisTemplate;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.lettuce.core.ScriptOutputType.VALUE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.assertThrows;

public class RedisScriptLoaderTest {

    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;

    @Before
    public  void beforeAll() {
        client = RedisClient.create("redis://localhost");
        connection = client.connect();
    }

    @After
    public  void afterAll() {
        client.shutdownAsync();
    }

    @Test
//    @DisplayName("should load rate limit lua script into Redis")
    public void shouldLoadScript() {
        RedisTemplate scriptLoader = new RedisTemplate(connection, "hello-world.lua");

        AssertionsForClassTypes.assertThat(scriptLoader.scriptSha()).isNotEmpty();
    }

    @Test
//    @DisplayName("should eagerly load rate limit lua script into Redis")
    public void shouldEagerlyLoadScript() {
        RedisTemplate scriptLoader = new RedisTemplate(connection, "hello-world.lua", true);

        AssertionsForClassTypes.assertThat(scriptLoader.scriptSha()).isNotEmpty();
    }

    @Test
//    @DisplayName("should fail if script not found")
    public void shouldFailedIfScriptNotFound() {

//        Throwable exception = assertThrows(IllegalArgumentException.class,
//                () -> new RedisTemplate(connection, "not-found-script.lua", true));
//        assertThat(exception.getMessage()).contains("not found");
    }

    @Test
//    @DisplayName("should fail if script not found")
    public void shouldExecuteScript() {

        RedisTemplate scriptLoader = new RedisTemplate(connection, "hello-world.lua", true);
        String sha = scriptLoader.scriptSha();

        Object result = connection.sync().evalsha(sha, VALUE);
        assertThat((String) result).isEqualTo("hello world");
    }
}
