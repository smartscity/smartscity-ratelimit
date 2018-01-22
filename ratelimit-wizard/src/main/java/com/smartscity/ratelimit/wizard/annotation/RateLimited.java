package com.smartscity.ratelimit.wizard.annotation;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface RateLimited {

    Key key();

    int limit();

    int duration();

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * If true the rate limiter won't enforce over limit. ReportOnly can be used to evaluate rate limits in production environments.
     * @return should not enforce limit.
     */
    boolean reportOnly() default false;

}
