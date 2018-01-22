package com.smartscity.ratelimit.wizard.filter;

import com.smartscity.ratelimit.core.limiter.request.RequestLimitRule;
import com.smartscity.ratelimit.core.limiter.request.RequestRateLimiter;
import com.smartscity.ratelimit.core.limiter.request.RequestRateLimiterFactory;
import com.smartscity.ratelimit.wizard.annotation.KeyProvider;
import com.smartscity.ratelimit.wizard.annotation.RateLimited;
import com.smartscity.ratelimit.wizard.annotation.RateLimiting;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.glassfish.jersey.server.model.AnnotatedMethod;

/**
 * <B>文件名称：</B>RateLimitFilter<BR>
 * <B>文件描述：</B><BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/22 上午11:40<BR>
 *
 * @author liyunlong  lyl2008dsg@163.com
 * @version 1.0
 **/


public class RateLimitFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimitFilter.class);

    @RateLimiting
    private RequestRateLimiterFactory factory;

    @Context
    private HttpServletRequest request;

    @Context
    private ResourceInfo resource;

    @Context
    SecurityContext securityContext;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try{
            AnnotatedMethod method = new AnnotatedMethod(resource.getResourceMethod());
            RateLimited rateLimited = method.getAnnotation(RateLimited.class);

            RequestRateLimiter rateLimit = factory.getInstance(toLimitRules(rateLimited));
            KeyProvider keyProvider = rateLimited.key();
            Optional<String> key = keyProvider.create(request, resource, securityContext);

            if(key.isPresent()){
                boolean overlimit = rateLimit.overLimit(key.get());
                if(overlimit) {
                    if (rateLimited.reportOnly()) {
                        LOG.info("rate limit key {} over limit. HTTP Status 429 returned.", key);
                        requestContext.abortWith(Response.status(HttpStatus.TOO_MANY_REQUESTS_429).build());
                    } else {
                        LOG.info("rate limit key {} over limit, and ReportOnly is true. no action taken.", key);
                    }
                    LOG.debug("rate limit key '{}' under limit.", key);
                }
            }else {
                LOG.warn("No key was provided by the key provide '{}'", keyProvider.getClass());
            }
        } catch (Exception e) {
            LOG.error("Filter Exception ", e);
        }
    }

    private Set<RequestLimitRule> toLimitRules(RateLimited rateLimited) {
//        return Arrays.stream(rateLimited.rates()).map(this::toLimitRule).collect(Collectors.toSet());
        Set<RequestLimitRule> set = new LinkedHashSet<>(); //Arrays.stream(rateLimited).collect(Collectors.toSet());
        RequestLimitRule requestLimitRule = RequestLimitRule.of(rateLimited.duration(), rateLimited.timeUnit(), rateLimited.limit());
        set.add(requestLimitRule);

        return set;
    }
}
