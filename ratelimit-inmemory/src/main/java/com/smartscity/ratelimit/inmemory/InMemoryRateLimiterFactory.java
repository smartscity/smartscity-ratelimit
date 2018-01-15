package com.smartscity.ratelimit.inmemory;

import com.smartscity.ratelimit.core.limiter.request.AbstractRequestRateLimiterFactory;
import com.smartscity.ratelimit.core.limiter.request.RequestLimitRule;
import com.smartscity.ratelimit.core.limiter.request.RequestRateLimiter;
import com.smartscity.ratelimit.core.limiter.request.RequestRateLimiterFactory;
import com.smartscity.ratelimit.inmemory.request.InMemorySlidingWindowRequestRateLimiter;

import java.io.IOException;
import java.util.Set;

/**
 * <B>文件名称：</B>InMemoryRateLimiterFactory<BR>
 * <B>文件描述：</B><BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/12 上午11:35<BR>
 *
 * @author apple  lyl2008dsg@163.com
 * @version 1.0
 **/


public class InMemoryRateLimiterFactory extends AbstractRequestRateLimiterFactory<RequestRateLimiter> {
    @Override
    public RequestRateLimiter getInstance(Set<RequestLimitRule> rules) {
        return lookupInstance(rules);
    }

    @Override
    protected InMemorySlidingWindowRequestRateLimiter create(Set<RequestLimitRule> rules) {
        return new InMemorySlidingWindowRequestRateLimiter(rules);
    }

    @Override
    public void close() throws IOException {

    }
}
