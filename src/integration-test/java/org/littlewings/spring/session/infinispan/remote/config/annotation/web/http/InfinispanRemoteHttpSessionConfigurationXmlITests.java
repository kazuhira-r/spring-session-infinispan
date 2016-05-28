package org.littlewings.spring.session.infinispan.remote.config.annotation.web.http;

import java.io.IOException;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.server.hotrod.HotRodServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.littlewings.spring.session.infinispan.AbstractInfinispanHttpSessionConfigurationXmlITests;
import org.littlewings.spring.session.infinispan.remote.RemoteCacheUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(Enclosed.class)
public class InfinispanRemoteHttpSessionConfigurationXmlITests extends AbstractInfinispanHttpSessionConfigurationXmlITests {
    private static HotRodServer server;

    @BeforeClass
    public static void setUpClass() {
        server = RemoteCacheUtils.startCacheServerConfigurationSpec("org/littlewings/spring/session/infinispan/remote/config/annotation/web/http/infinispan-custom-hotrod.xml");
    }

    @AfterClass
    public static void tearDownClass() {
        server.stop();
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration
    @WebAppConfiguration
    public static class CustomXmlCacheNameTest extends AbstractCustomXmlCacheNameTest {
        @Configuration
        @EnableInfinispanRemoteHttpSession(sessionsCacheName = "mySessions")
        static class InfinispanSessionXmlConfigCustomCacheName {
            @Bean(destroyMethod = "stop")
            public RemoteCacheManager cacheManager() throws IOException {
                return RemoteCacheUtils.cacheManager();
            }
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration
    @WebAppConfiguration
    public static class CustomXmlCacheNameAndIdleTest extends AbstractCustomXmlCacheNameAndIdleTest {
        @Configuration
        @EnableInfinispanRemoteHttpSession(sessionsCacheName = "testSessions", maxInactiveIntervalInSeconds = 1200)
        static class InfinispanSessionXmlConfigCustomCacheNameAndIdle {
            @Bean(destroyMethod = "stop")
            public RemoteCacheManager cacheManager() {
                return RemoteCacheUtils.cacheManager();
            }
        }
    }
}
