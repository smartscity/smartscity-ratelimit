package com.smartscity.ratelimit.core.limiter.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <B>文件名称：</B>AbstractRequestRateLimiterFactory<BR>
 * <B>文件描述：</B><BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/12 上午11:03<BR>
 *
 * @author apple  lyl2008dsg@163.com
 * @version 1.0
 **/


public abstract class AbstractRequestRateLimiterFactory<T> implements RequestRateLimiterFactory {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRequestRateLimiterFactory.class.getClass());

    private final ConcurrentMap<Set<RequestLimitRule>, T> rateLimiterInstances = new ConcurrentHashMap<>();

    protected abstract T create(Set<RequestLimitRule> rules);

    protected T lookupInstance(Set<RequestLimitRule> rules) {
        T rateLimiter = rateLimiterInstances.get(rules);
        if (rateLimiter == null) {
            LOG.info("creating new RequestRateLimiter");
            rateLimiterInstances.putIfAbsent(rules, create(rules));
            // small race condition window, so lookup again
            rateLimiter = rateLimiterInstances.get(rules);
        }
        return rateLimiter;
    }

}
