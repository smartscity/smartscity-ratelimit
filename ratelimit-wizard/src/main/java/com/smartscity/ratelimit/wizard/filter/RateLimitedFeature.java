package com.smartscity.ratelimit.wizard.filter;

import com.smartscity.ratelimit.wizard.annotation.RateLimited;
import org.glassfish.jersey.server.model.AnnotatedMethod;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

/**
 * <B>文件名称：</B>RateLimitedFeature<BR>
 * <B>文件描述：</B><BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/22 下午2:17<BR>
 *
 * @author apple  lyl2008dsg@163.com
 * @version 1.0
 **/


public class RateLimitedFeature implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resource, FeatureContext context) {
        final AnnotatedMethod method = new AnnotatedMethod(resource.getResourceMethod());
        final RateLimited rateLimited = method.getAnnotation(RateLimited.class);

        if(null != rateLimited){
            context.register(RateLimitFilter.class);
        }

    }
}
