package org.littlewings.spring.session.infinispan.remote;

import java.util.Arrays;
import java.util.function.Function;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.equivalence.AnyServerEquivalence;
import org.infinispan.configuration.cache.ConfigurationChildBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.server.hotrod.HotRodServer;
import org.infinispan.server.hotrod.configuration.HotRodServerConfigurationBuilder;
import org.littlewings.spring.session.infinispan.embedded.EmbeddedCacheUtils;

public interface RemoteCacheUtils {
    String HOST = "localhost";

    static org.infinispan.configuration.cache.Configuration defaultConfiguration() {
        return defaultConfigurationBuilder().build();
    }

    static org.infinispan.configuration.cache.ConfigurationChildBuilder defaultConfigurationBuilder() {
        return new org.infinispan.configuration.cache.ConfigurationBuilder()
                .dataContainer()
                .keyEquivalence(new AnyServerEquivalence())
                .valueEquivalence(new AnyServerEquivalence())
                .expiration();
    }

    static HotRodServer startCacheServerDefaultConfiguration(String cacheName, int port) {
        EmbeddedCacheManager embeddedCacheManager = EmbeddedCacheUtils.cacheManager(cacheName, defaultConfiguration());
        HotRodServer server = new HotRodServer();
        server.start(new HotRodServerConfigurationBuilder()
                        .host(HOST)
                        .port(port)
                        .build(),
                embeddedCacheManager);
        return server;
    }

    static HotRodServer startCacheServerDefaultConfiguration(String cacheName, int port, Function<ConfigurationChildBuilder, ConfigurationChildBuilder> appendBuilder) {
        EmbeddedCacheManager embeddedCacheManager =
                EmbeddedCacheUtils.cacheManager(cacheName, appendBuilder.apply(defaultConfigurationBuilder()).build());
        HotRodServer server = new HotRodServer();
        server.start(new HotRodServerConfigurationBuilder()
                        .host(HOST)
                        .port(port)
                        .build(),
                embeddedCacheManager);
        return server;
    }


    static HotRodServer startCacheServerConfigurationSpec(String configurationPath, int port, String... useCacheNames) {
        EmbeddedCacheManager embeddedCacheManager = EmbeddedCacheUtils.cacheManager(configurationPath);
        Arrays.stream(useCacheNames).forEach(cacheName -> embeddedCacheManager.getCache(cacheName));

        HotRodServer server = new HotRodServer();
        server.start(new HotRodServerConfigurationBuilder()
                        .host(HOST)
                        .port(port)
                        .build(),
                embeddedCacheManager);
        return server;
    }

    static RemoteCacheManager cacheManager(int port) {
        return new RemoteCacheManager(new ConfigurationBuilder().addServer().host(HOST).port(port).build());
    }
}
