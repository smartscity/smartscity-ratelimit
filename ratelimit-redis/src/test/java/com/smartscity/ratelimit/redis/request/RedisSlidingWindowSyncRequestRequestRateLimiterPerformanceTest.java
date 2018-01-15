package com.smartscity.ratelimit.redis.request;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import com.smartscity.ratelimit.core.limiter.request.RequestLimitRule;
import com.smartscity.ratelimit.core.limiter.request.RequestRateLimiter;
import com.smartscity.ratelimit.core.time.TimeBanditSupport;
import com.smartscity.ratelimit.core.time.TimeSupport;
import com.smartscity.ratelimit.redis.utils.RedisTemplate;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


public class RedisSlidingWindowSyncRequestRequestRateLimiterPerformanceTest  {

    private static RedisClient client;
    private static StatefulRedisConnection<String, String> connect;

    @Before
    public void beforeAll() {
        client = RedisClient.create("redis://localhost:6379");
        connect = client.connect();
    }

    @After
    public void afterAll() {
        client.shutdownAsync();
    }

    @After
    public void afterEach() {
//        try (StatefulRedisConnection<String, String> connection = client.connect()) {
//        connect.sync().flushdb();
//        }
    }

    protected RequestRateLimiter getRateLimiter(Set<RequestLimitRule> rules, TimeSupport timeSupplier) {
        return new RedisSlidingWindowRequestRateLimiter(connect, rules, timeSupplier);
    }


    private final Logger log = LoggerFactory.getLogger(getClass());

    private final TimeBanditSupport timeBandit = new TimeBanditSupport();

    @Test
    public void shouldLimitDualWindowSyncTimed() {

        Stopwatch watch = Stopwatch.createStarted();

        ImmutableSet<RequestLimitRule> rules =
                ImmutableSet.of(RequestLimitRule.of(2, TimeUnit.SECONDS, 100), RequestLimitRule.of(10, TimeUnit.SECONDS, 100));
        RequestRateLimiter requestRateLimiter = getRateLimiter(rules, timeBandit);
        Random rand = new Random();

        int total = 10_000;
        IntStream.rangeClosed(1, total).map(i -> rand.nextInt(128)).forEach(value -> {
            timeBandit.addUnixTimeMilliSeconds(200L);
            requestRateLimiter.overLimit("ip:127.0.0." + value);
        });

        double transactionsPerSecond = Math.ceil((double) total / watch.elapsed(TimeUnit.MILLISECONDS) * 1000);

        log.info("total time {} checks {}/sec", watch.stop(), NumberFormat.getNumberInstance(Locale.US).format(transactionsPerSecond));
    }

    @Test
    public void set(){
        RedisTemplate redisTemplate = new RedisTemplate(connect);
        redisTemplate.set("key", "f", "hello");
        String value = redisTemplate.get("key", "f");
        System.out.println(value);
    }
}
