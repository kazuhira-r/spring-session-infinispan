package org.littlewings.spring.session.infinispan.embedded.config.annotation.web.http;

import java.util.Map;
import javax.annotation.PreDestroy;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.littlewings.spring.session.infinispan.ExpiringSessionMap;
import org.littlewings.spring.session.infinispan.embedded.EmbeddedSessionEntryListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.session.ExpiringSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;

@Configuration
public class InfinispanEmbeddedHttpSessionConfiguration extends SpringHttpSessionConfiguration implements ImportAware {
    private Integer maxInactiveIntervalInSeconds = 1800;

    private String sessionsCacheName = "springSessions";

    private Cache<String, ExpiringSession> sessionsCache;

    private EmbeddedSessionEntryListener sessionListener;

    @Bean
    public SessionRepository<ExpiringSession> sessionRepository(EmbeddedCacheManager cacheManager, EmbeddedSessionEntryListener sessionListener) {
        this.sessionListener = sessionListener;

        sessionsCache = cacheManager.getCache(sessionsCacheName);
        sessionsCache.addListener(sessionListener);

        MapSessionRepository sessionRepository =
                new MapSessionRepository(new ExpiringSessionMap(sessionsCache,
                        sessionsCache.getCacheConfiguration().expiration().lifespan()));
        sessionRepository
                .setDefaultMaxInactiveInterval(maxInactiveIntervalInSeconds);

        return sessionRepository;
    }

    @PreDestroy
    void removeSessionListener() {
        sessionsCache.removeListener(sessionListener);
    }

    @Bean
    public EmbeddedSessionEntryListener sessionListener(ApplicationEventPublisher eventPublisher) {
        return new EmbeddedSessionEntryListener(eventPublisher);
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> enableAttrMap = importMetadata
                .getAnnotationAttributes(EnableInfinispanEmbeddedHttpSession.class.getName());
        AnnotationAttributes enableAttrs = AnnotationAttributes.fromMap(enableAttrMap);

        transferAnnotationAttributes(enableAttrs);
    }

    private void transferAnnotationAttributes(AnnotationAttributes enableAttrs) {
        setMaxInactiveIntervalInSeconds(
                (Integer) enableAttrs.getNumber("maxInactiveIntervalInSeconds"));
        setSessionsCacheName(enableAttrs.getString("sessionsCacheName"));
    }


    public void setMaxInactiveIntervalInSeconds(int maxInactiveIntervalInSeconds) {
        this.maxInactiveIntervalInSeconds = maxInactiveIntervalInSeconds;
    }

    public void setSessionsCacheName(String sessionsCacheName) {
        this.sessionsCacheName = sessionsCacheName;
    }
}
