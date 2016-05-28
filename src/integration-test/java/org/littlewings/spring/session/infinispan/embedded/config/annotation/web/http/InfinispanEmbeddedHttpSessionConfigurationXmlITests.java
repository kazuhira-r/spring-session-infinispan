package org.littlewings.spring.session.infinispan.embedded.config.annotation.web.http;

import java.io.IOException;

import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.littlewings.spring.session.infinispan.AbstractInfinispanHttpSessionConfigurationXmlITests;
import org.littlewings.spring.session.infinispan.embedded.EmbeddedCacheUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(Enclosed.class)
public class InfinispanEmbeddedHttpSessionConfigurationXmlITests extends AbstractInfinispanHttpSessionConfigurationXmlITests {
    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration
    @WebAppConfiguration
    public static class CustomXmlCacheNameTest extends AbstractCustomXmlCacheNameTest {
        @Configuration
        @EnableInfinispanEmbeddedHttpSession(sessionsCacheName = "mySessions")
        static class InfinispanSessionXmlConfigCustomCacheName {
            @Bean(destroyMethod = "stop")
            public EmbeddedCacheManager cacheManager() throws IOException {
                return EmbeddedCacheUtils
                        .cacheManager("org/littlewings/spring/session/infinispan/embedded/config/annotation/web/http/infinispan-custom.xml");
            }
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration
    @WebAppConfiguration
    public static class CustomXmlCacheNameAndIdleTest extends AbstractCustomXmlCacheNameAndIdleTest {
        @Configuration
        @EnableInfinispanEmbeddedHttpSession(sessionsCacheName = "testSessions", maxInactiveIntervalInSeconds = 1200)
        static class InfinispanSessionXmlConfigCustomCacheNameAndIdle {
            @Bean(destroyMethod = "stop")
            public EmbeddedCacheManager cacheManager() {
                return EmbeddedCacheUtils
                        .cacheManager("org/littlewings/spring/session/infinispan/embedded/config/annotation/web/http/infinispan-custom.xml");
            }
        }
    }
}
