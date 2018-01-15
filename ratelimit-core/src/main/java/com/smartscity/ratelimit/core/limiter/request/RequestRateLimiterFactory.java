package com.smartscity.ratelimit.core.limiter.request;


import java.io.Closeable;
import java.util.Set;

public interface RequestRateLimiterFactory extends Closeable {

    RequestRateLimiter getInstance(Set<RequestLimitRule> rules);


}
