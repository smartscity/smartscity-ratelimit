package com.smartscity.ratelimit.redis.request;

import com.smartscity.ratelimit.core.limiter.request.RequestLimitRule;
import com.smartscity.ratelimit.core.limiter.request.RequestRateLimiter;
import com.smartscity.ratelimit.core.time.SystemTimeSupport;
import com.smartscity.ratelimit.core.time.TimeSupport;
import com.smartscity.ratelimit.redis.utils.JsonSerialiser;
import com.smartscity.ratelimit.redis.utils.RedisTemplate;
import io.lettuce.core.api.StatefulRedisConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static io.lettuce.core.ScriptOutputType.VALUE;
import static java.util.Objects.requireNonNull;

/**
 * <B>文件名称：</B>RedisSlidingWindowRequestRateLimiter<BR>
 * <B>文件描述：</B><BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/12 下午3:51<BR>
 *
 * @author apple  lyl2008dsg@163.com
 * @version 1.0
 **/


public class RedisSlidingWindowRequestRateLimiter implements RequestRateLimiter {


    private static final Logger LOG = LoggerFactory.getLogger(RedisSlidingWindowRequestRateLimiter.class);

    private static final Duration BLOCK_TIMEOUT = Duration.of(5, ChronoUnit.SECONDS);

    private final JsonSerialiser serialiser = new JsonSerialiser();

    private final StatefulRedisConnection<String, String> connection;
    private final RedisTemplate scriptLoader;
    private final String rulesJson;
    private final TimeSupport timeSupport;


    public RedisSlidingWindowRequestRateLimiter(StatefulRedisConnection<String, String> connection, Set<RequestLimitRule> rules) {
        this(connection, rules, new SystemTimeSupport());
    }

    public RedisSlidingWindowRequestRateLimiter(StatefulRedisConnection<String, String> connection, Set<RequestLimitRule> rules, TimeSupport timeSupport) {
        this.connection = connection;
        scriptLoader = new RedisTemplate(connection, "sliding-window-ratelimit.lua");
        rulesJson = serialiser.encode(rules);
        this.timeSupport = timeSupport;
    }

    @Override
    public boolean overLimit(String key) {
        return overLimit(key, 1);
    }

    @Override
    public boolean overLimit(String key, int weight) {
        return throwOnTimeout(eqOrGeLimitReactive(key, weight, true));
    }

    @Override
    public boolean lessLimit(String key) {
        return false;
    }

    @Override
    public boolean lessLimit(String key, int weight) {
        return false;
    }

    @Override
    public boolean resetLimit(String key) {
        return false;
    }

    private Mono<Boolean> eqOrGeLimitReactive(String key, int weight, boolean strictlyGreater) {
        requireNonNull(key);

        // TODO script load can be reactive
        // TODO handle scenario where script is not loaded, flush scripts and test scenario
        String sha = scriptLoader.scriptSha();

        return timeSupport.getReactive().flatMapMany(time ->
                connection.reactive().evalsha(sha, VALUE, new String[]{key}, rulesJson, Long.toString(time), Integer.toString(weight), toStringOneZero(strictlyGreater)))
                .next()
                .map("1"::equals)
                .doOnSuccess(over -> {
                    if (over) {
                        LOG.debug("Requests matched by key '{}' incremented by weight {} are greater than {}the limit", key, weight, strictlyGreater ? "" : "or equal to ");
                    }
                });
    }

    private String toStringOneZero(boolean strictlyGreater) {
        return strictlyGreater ? "1" : "0";
    }

    private boolean throwOnTimeout(Mono<Boolean> mono) {
        Boolean result = mono.block(BLOCK_TIMEOUT);
        if (result == null) {
            throw new RuntimeException("waited " + BLOCK_TIMEOUT + "before timing out");
        }
        return result;
    }
}
