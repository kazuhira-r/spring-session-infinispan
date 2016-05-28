package org.littlewings.spring.session.infinispan.embedded;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public interface EmbeddedCacheUtils {
    static EmbeddedCacheManager cacheManager() {
        return new DefaultCacheManager(new GlobalConfigurationBuilder().globalJmxStatistics().allowDuplicateDomains(true).build());
    }

    static EmbeddedCacheManager cacheManager(String configurationPath) {
        try {
            return new DefaultCacheManager(configurationPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static EmbeddedCacheManager cacheManager(String cacheName, Configuration configuration) {
        EmbeddedCacheManager manager = cacheManager();
        manager.defineConfiguration(cacheName, configuration);
        return manager;
    }
}
