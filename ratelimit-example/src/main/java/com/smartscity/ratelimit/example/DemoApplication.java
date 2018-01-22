package com.smartscity.ratelimit.example;

import com.smartscity.ratelimit.core.limiter.request.RequestRateLimiterFactory;
import com.smartscity.ratelimit.example.api.LoginResource;
import com.smartscity.ratelimit.example.api.UserResource;
import com.smartscity.ratelimit.example.auth.TestOAuthAuthenticator;
import com.smartscity.ratelimit.inmemory.InMemoryRateLimiterFactory;
import com.smartscity.ratelimit.redis.RedisRateLimiterFactory;
import com.smartscity.ratelimit.wizard.RateLimitBundle;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class DemoApplication extends Application<Configuration> {

//	public static void main(String[] args) {
//		SpringApplication.run(DemoApplication.class, args);
//	}

	private RedisClient redisClient;

	public void initialize(Bootstrap<Configuration> bootstrap) {
		redisClient = RedisClient.create("redis://localhost");
		RequestRateLimiterFactory factory = new RedisRateLimiterFactory(redisClient);

//		RequestRateLimiterFactory factory = new InMemoryRateLimiterFactory();

		bootstrap.addBundle(new RateLimitBundle(factory));
	}

	@Override
	public void run(Configuration configuration, Environment environment) throws Exception {
		environment.jersey().register(new LoginResource());
		environment.jersey().register(new UserResource());

		environment.jersey().register(new AuthDynamicFeature(
				new OAuthCredentialAuthFilter.Builder<PrincipalImpl>()
						.setAuthenticator(new TestOAuthAuthenticator()).setPrefix("Bearer")
						.buildAuthFilter()));
		environment.jersey().register(RolesAllowedDynamicFeature.class);
		environment.jersey().register(new AuthValueFactoryProvider.Binder<>(PrincipalImpl.class));

		//TODO move this cleanup into the tests
		environment.lifecycle().manage(new Managed() {
			@Override
			public void start() {
			}

			@Override
			public void stop() {
				flushRedis();
			}

			private void flushRedis() {
				try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
					connection.sync().flushdb();
				}
				redisClient.shutdownAsync();
			}
		});
	}
}
