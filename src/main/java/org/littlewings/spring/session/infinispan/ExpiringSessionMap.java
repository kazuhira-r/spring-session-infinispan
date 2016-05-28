package org.littlewings.spring.session.infinispan;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.infinispan.commons.api.BasicCache;
import org.springframework.session.ExpiringSession;

public class ExpiringSessionMap implements Map<String, ExpiringSession> {
    private BasicCache<String, ExpiringSession> cache;

    private long timeToLive = 0L;

    public ExpiringSessionMap(BasicCache<String, ExpiringSession> cache) {
        this.cache = cache;
    }

    public ExpiringSessionMap(BasicCache<String, ExpiringSession> cache, long timeToLiveMilliSeconds) {
        this(cache);
        this.timeToLive = timeToLiveMilliSeconds;
    }

    @Override
    public ExpiringSession put(String key, ExpiringSession value) {
        if (value == null) {
            return cache.put(key, value);
        }

        return cache.put(key, value, timeToLive, TimeUnit.MILLISECONDS, value.getMaxInactiveIntervalInSeconds(),
                TimeUnit.SECONDS);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    @Override
    public ExpiringSession get(Object key) {
        return cache.get(key);
    }

    @Override
    public ExpiringSession remove(Object key) {
        return cache.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends ExpiringSession> m) {
        cache.putAll(m);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public Set<String> keySet() {
        return cache.keySet();
    }

    @Override
    public Collection<ExpiringSession> values() {
        return cache.values();
    }

    @Override
    public Set<Entry<String, ExpiringSession>> entrySet() {
        return cache.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return cache.equals(o);
    }

    @Override
    public int hashCode() {
        return cache.hashCode();
    }

}
