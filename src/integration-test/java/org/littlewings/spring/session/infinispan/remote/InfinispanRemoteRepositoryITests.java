package org.littlewings.spring.session.infinispan.remote;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.server.hotrod.HotRodServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.littlewings.spring.session.infinispan.remote.config.annotation.web.http.EnableInfinispanRemoteHttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.ExpiringSession;
import org.springframework.session.SessionRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.SocketUtils;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class InfinispanRemoteRepositoryITests<S extends ExpiringSession> {
    private static HotRodServer server;
    private static int port;

    @Autowired
    private RemoteCacheManager cacheManager;

    @Autowired
    private SessionRepository<S> repository;

    @BeforeClass
    public static void setUpClass() {
        port = SocketUtils.findAvailableTcpPort();
        server = RemoteCacheUtils.startCacheServerDefaultConfiguration("springSessions", port);
    }

    @AfterClass
    public static void tearDownClass() {
        server.stop();
    }

    @Test
    public void createAndDestroySession() {
        S sessionToSave = repository.createSession();
        String sessionId = sessionToSave.getId();

        RemoteCache<String, S> cache = cacheManager.getCache("springSessions");

        assertThat(cache.size()).isEqualTo(0);

        repository.save(sessionToSave);

        assertThat(cache.size()).isEqualTo(1);
        assertThat(cache.get(sessionId)).isEqualTo(sessionToSave);

        repository.delete(sessionId);

        assertThat(cache.size()).isEqualTo(0);
    }

    @Configuration
    @EnableInfinispanRemoteHttpSession
    static class InfinispanSessionConfig {
        @Bean(destroyMethod = "stop")
        public RemoteCacheManager cacheManager() {
            return RemoteCacheUtils.cacheManager(port);
        }
    }
}
