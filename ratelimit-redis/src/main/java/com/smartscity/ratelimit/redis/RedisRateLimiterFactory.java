package com.smartscity.ratelimit.redis;

import com.smartscity.ratelimit.core.limiter.request.AbstractRequestRateLimiterFactory;
import com.smartscity.ratelimit.core.limiter.request.RequestLimitRule;
import com.smartscity.ratelimit.core.limiter.request.RequestRateLimiter;
import com.smartscity.ratelimit.redis.request.RedisSlidingWindowRequestRateLimiter;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

import java.io.IOException;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * <B>文件名称：</B>RedisRateLimiterFactory<BR>
 * <B>文件描述：</B><BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/12 下午3:49<BR>
 *
 * @author apple  lyl2008dsg@163.com
 * @version 1.0
 **/


public class RedisRateLimiterFactory extends AbstractRequestRateLimiterFactory<RedisSlidingWindowRequestRateLimiter> {

    private final RedisClient client;
    private StatefulRedisConnection<String, String> connection;

    public RedisRateLimiterFactory(RedisClient client) {
        this.client = requireNonNull(client);
    }

    @Override
    public RequestRateLimiter getInstance(Set<RequestLimitRule> rules) {
        return lookupInstance(rules);
    }

    @Override
    protected RedisSlidingWindowRequestRateLimiter create(Set<RequestLimitRule> rules) {
        return new RedisSlidingWindowRequestRateLimiter(getConnection(), rules);
    }

    @Override
    public void close() {
        client.shutdownAsync();
    }

    private StatefulRedisConnection<String, String> getConnection() {
        // going to ignore race conditions at the cost of having multiple connections
        if (connection == null) {
            connection = client.connect();
        }
        return connection;
    }
}
