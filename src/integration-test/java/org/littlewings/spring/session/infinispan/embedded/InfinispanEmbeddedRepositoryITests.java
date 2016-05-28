package org.littlewings.spring.session.infinispan.embedded;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.littlewings.spring.session.infinispan.embedded.config.annotation.web.http.EnableInfinispanEmbeddedHttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.ExpiringSession;
import org.springframework.session.SessionRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class InfinispanEmbeddedRepositoryITests<S extends ExpiringSession> {
    @Autowired
    private EmbeddedCacheManager cacheManager;

    @Autowired
    private SessionRepository<S> repository;

    @Test
    public void createAndDestroySession() {
        S sessionToSave = repository.createSession();
        String sessionId = sessionToSave.getId();

        Cache<String, S> cache = cacheManager.getCache("springSessions");

        assertThat(cache.size()).isEqualTo(0);

        repository.save(sessionToSave);

        assertThat(cache.size()).isEqualTo(1);
        assertThat(cache.get(sessionId)).isEqualTo(sessionToSave);

        repository.delete(sessionId);

        assertThat(cache.size()).isEqualTo(0);
    }

    @Configuration
    @EnableInfinispanEmbeddedHttpSession
    static class InfinispanSessionConfig {
        @Bean(destroyMethod = "stop")
        public EmbeddedCacheManager cacheManager() {
            return EmbeddedCacheUtils.cacheManager();
        }
    }
}
