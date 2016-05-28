package org.littlewings.spring.session.infinispan.embedded.config.annotation.web.http;

import java.util.concurrent.TimeUnit;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.runner.RunWith;
import org.littlewings.spring.session.infinispan.AbstractEnableInfinispanHttpSessionEventsITests;
import org.littlewings.spring.session.infinispan.embedded.EmbeddedCacheUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.SessionEventRegistry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class EnableInfinispanEmbeddedHttpSessionEventsITests extends AbstractEnableInfinispanHttpSessionEventsITests {
    @Configuration
    @EnableInfinispanEmbeddedHttpSession(maxInactiveIntervalInSeconds = MAX_INACTIVE_INTERVAL_IN_SECONDS, sessionsCacheName = SESSION_CACHE_NAME)
    static class InfinispanSessionConfig {
        @Bean(destroyMethod = "stop")
        public EmbeddedCacheManager cacheManager() {
            return EmbeddedCacheUtils.cacheManager(SESSION_CACHE_NAME,
                    new ConfigurationBuilder().expiration().wakeUpInterval(1L, TimeUnit.SECONDS).build());
        }

        @Bean
        public SessionEventRegistry sessionEventRegistry() {
            return new SessionEventRegistry();
        }
    }
}
