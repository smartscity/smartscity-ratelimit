package com.smartscity.ratelimit.inmemory.request;

import com.smartscity.ratelimit.core.limiter.request.RequestLimitRule;
import com.smartscity.ratelimit.core.limiter.request.RequestRateLimiter;
import com.smartscity.ratelimit.core.time.SystemTimeSupport;
import com.smartscity.ratelimit.core.time.TimeSupport;
import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;
import net.jodah.expiringmap.ExpiringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * <B>文件名称：</B>InMemorySlidingWindowRequestRateLimiter<BR>
 * <B>文件描述：</B><BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/12 上午11:31<BR>
 *
 * @author apple  lyl2008dsg@163.com
 * @version 1.0
 **/


public class InMemorySlidingWindowRequestRateLimiter implements RequestRateLimiter {

    private static final Logger LOG = LoggerFactory.getLogger(InMemorySlidingWindowRequestRateLimiter.class);

    private final Set<RequestLimitRule> rules;
    private final TimeSupport timeSupport;
    private final ExpiringMap<String, ConcurrentMap<String, Long>> expiringKeyMap;
    private final KeyLockManager lockManager = KeyLockManagers.newLock();

    public InMemorySlidingWindowRequestRateLimiter(Set<RequestLimitRule> rules) {
        this(rules, new SystemTimeSupport());
    }

    public InMemorySlidingWindowRequestRateLimiter(Set<RequestLimitRule> rules, TimeSupport timeSupport) {
        this.rules = rules;
        this.timeSupport = timeSupport;
        this.expiringKeyMap = ExpiringMap.builder().variableExpiration().build();
    }

    InMemorySlidingWindowRequestRateLimiter(ExpiringMap<String, ConcurrentMap<String, Long>> expiringKeyMap, Set<RequestLimitRule> rules, TimeSupport timeSupport) {
        this.expiringKeyMap = expiringKeyMap;
        this.rules = rules;
        this.timeSupport = timeSupport;
    }


    @Override
    public boolean overLimit(String key) {
        return false;
    }

    @Override
    public boolean overLimit(String key, int weight) {
        return false;
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
}
