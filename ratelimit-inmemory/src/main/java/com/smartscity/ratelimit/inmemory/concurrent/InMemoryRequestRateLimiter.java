package com.smartscity.ratelimit.inmemory.concurrent;

import com.smartscity.ratelimit.core.limiter.concurrent.Baton;
import com.smartscity.ratelimit.core.limiter.concurrent.LimitRule;
import com.smartscity.ratelimit.core.limiter.concurrent.RequestLimiter;
import net.jodah.expiringmap.ExpiringMap;

import java.util.concurrent.Semaphore;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.jodah.expiringmap.ExpirationPolicy.ACCESSED;

/**
 * <B>文件名称：</B>InMemoryRequestRateLimiter<BR>
 * <B>文件描述：</B><BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/12 上午11:18<BR>
 *
 * @author apple  lyl2008dsg@163.com
 * @version 1.0
 **/
public class InMemoryRequestRateLimiter implements RequestLimiter {

    private final ExpiringMap<String, Semaphore> expiringKeyMap;
    private final LimitRule rule;

    public InMemoryRequestRateLimiter( LimitRule rule) {
        this.rule = rule;
        expiringKeyMap = ExpiringMap.builder().expiration(rule.getTimeoutMillis(), rule.getTimeUnit()).expirationPolicy(ACCESSED).build();
    }

    @Override
    public Baton acquire(String key) {
        return acquire(key, 1);
    }

    @Override
    public Baton acquire(String key, int weight) {
        final Semaphore semaphore = expiringKeyMap.computeIfAbsent(key, s -> new Semaphore(rule.getLimit(), false));
        if (semaphore.tryAcquire(weight)) {

            // TODO the semaphore needs to expire the if never closed
            return new InMemoryBaton(semaphore, weight);
        }
        return new InMemoryBaton(weight);
    }
}
