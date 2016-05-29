package org.littlewings.spring.session.infinispan.remote.config.annotation.web.http;

import java.util.concurrent.TimeUnit;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.server.hotrod.HotRodServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.littlewings.spring.session.infinispan.AbstractEnableInfinispanHttpSessionEventsITests;
import org.littlewings.spring.session.infinispan.remote.RemoteCacheUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.SessionEventRegistry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.SocketUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class EnableInfinispanRemoteHttpSessionEventsITests extends AbstractEnableInfinispanHttpSessionEventsITests {
    private static HotRodServer server;
    private static int port;

    @BeforeClass
    public static void setUpClass() {
        port = SocketUtils.findAvailableTcpPort();
        server =
                RemoteCacheUtils
                        .startCacheServerDefaultConfiguration(SESSION_CACHE_NAME,
                                port,
                                cb -> cb.expiration().wakeUpInterval(1L, TimeUnit.SECONDS));
    }

    @AfterClass
    public static void tearDownClass() {
        server.stop();
    }

    @Configuration
    @EnableInfinispanRemoteHttpSession(maxInactiveIntervalInSeconds = MAX_INACTIVE_INTERVAL_IN_SECONDS, sessionsCacheName = SESSION_CACHE_NAME)
    static class InfinispanSessionConfig {
        @Bean(destroyMethod = "stop")
        public RemoteCacheManager cacheManager() {
            return RemoteCacheUtils.cacheManager(port);
        }

        @Bean
        public SessionEventRegistry sessionEventRegistry() {
            return new SessionEventRegistry();
        }
    }
}
