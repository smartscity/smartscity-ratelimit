package com.smartscity.ratelimit.wizard;

import com.smartscity.ratelimit.core.limiter.request.RequestRateLimiterFactory;
import com.smartscity.ratelimit.wizard.annotation.RateLimiting;
import com.smartscity.ratelimit.wizard.filter.RateLimitFilter;
import com.smartscity.ratelimit.wizard.filter.RateLimitedFeature;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

import javax.inject.Singleton;

/**
 * <B>文件名称：</B>RateLimitBundle<BR>
 * <B>文件描述：</B><BR>
 * <BR>
 * <B>版权声明：</B>(C)2016-2018<BR>
 * <B>公司部门：</B>SMARTSCITY Technology<BR>
 * <B>创建时间：</B>2018/1/22 上午11:01<BR>
 *
 * @author liyunlong  lyl2008dsg@163.com
 * @version 1.0
 **/


public class RateLimitBundle implements ConfiguredBundle<Configuration> {

    private final RequestRateLimiterFactory requestRateLimiterFactory;

    public RateLimitBundle(RequestRateLimiterFactory requestRateLimiterFactory) {
        this.requestRateLimiterFactory = requestRateLimiterFactory;
    }


    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        environment.jersey().register(new RateLimitingFactoryProvider.Binder(requestRateLimiterFactory));
        environment.jersey().register(new RateLimitedFeature());

        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {

            }

            @Override
            public void stop() throws Exception {
                requestRateLimiterFactory.close();
            }
        });
    }

    @Singleton
    public static class RateLimitingFactoryProvider extends AbstractValueFactoryProvider {

        private RequestRateLimiterFactory requestRateLimiterFactory;

        /**
         * Initialize the provider.
         *
         * @param extractorProvider              multivalued map parameter extractor provider.
         * @param injector           HK2 service locator.
         * @param rateLimiterFactoryProvider
         */
        protected RateLimitingFactoryProvider(
                final MultivaluedParameterExtractorProvider extractorProvider,
                final ServiceLocator injector,
                final RateLimiterFactoryProvider rateLimiterFactoryProvider) {
            super(extractorProvider, injector, Parameter.Source.UNKNOWN);
            this.requestRateLimiterFactory = rateLimiterFactoryProvider.factory;
        }

        @Override
        protected Factory<RequestRateLimiterFactory> createValueFactory(Parameter parameter) {
            final RateLimiting annotation = parameter.getAnnotation(RateLimiting.class);
            if(null == annotation) {
                return null;
            } else {
                return new AbstractContainerRequestValueFactory<RequestRateLimiterFactory>() {
                    @Override
                    public RequestRateLimiterFactory provide() {
                        return requestRateLimiterFactory;
                    }
                };
            }
        }

        public static class RateLimitingInjectionResolver extends ParamInjectionResolver<RateLimiting> {
            public RateLimitingInjectionResolver() {
                super(RateLimitingFactoryProvider.class);
            }
        }

        @Singleton
        public static class RateLimiterFactoryProvider {
            private final RequestRateLimiterFactory factory;

            private RateLimiterFactoryProvider(final RequestRateLimiterFactory factory) {
                this.factory = factory;
            }
        }

        public static class Binder extends AbstractBinder {

            private final RequestRateLimiterFactory requestRateLimiterFactory;

            public Binder(final RequestRateLimiterFactory requestRateLimiterFactory) {
                this.requestRateLimiterFactory = requestRateLimiterFactory;
            }

            @Override
            protected void configure() {
                bind(new RateLimiterFactoryProvider(requestRateLimiterFactory)).to(RateLimiterFactoryProvider.class);
                bind(RateLimitingFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
                bind(RateLimitingFactoryProvider.RateLimitingInjectionResolver.class)
                        .to(new TypeLiteral<InjectionResolver<RateLimiting>>() {}).in(Singleton.class);
            }
        }


    }
}
