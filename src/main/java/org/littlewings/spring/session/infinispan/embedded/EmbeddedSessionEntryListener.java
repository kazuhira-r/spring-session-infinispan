package org.littlewings.spring.session.infinispan.embedded;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntriesEvicted;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryExpired;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntriesEvictedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryExpiredEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.session.ExpiringSession;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionExpiredEvent;
import org.springframework.util.Assert;

@Listener(/* clustered = true, */ observation = Listener.Observation.POST)
public class EmbeddedSessionEntryListener {
    private Log logger = LogFactory.getLog(EmbeddedSessionEntryListener.class);

    private ApplicationEventPublisher eventPublisher;

    public EmbeddedSessionEntryListener(ApplicationEventPublisher eventPublisher) {
        Assert.notNull(eventPublisher, "eventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }

    @CacheEntryCreated
    public void entryCreated(CacheEntryCreatedEvent<String, ExpiringSession> event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Session created with id: " + event.getValue().getId());
        }
        eventPublisher.publishEvent(new SessionCreatedEvent(this, event.getValue()));
    }

    @CacheEntryExpired
    public void entryExpired(CacheEntryExpiredEvent<String, ExpiringSession> event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Session expired with id: " + event.getValue().getId());
        }

        eventPublisher.publishEvent(new SessionExpiredEvent(this, event.getValue()));
    }

    /*
    @CacheEntriesEvicted
    public void entriesEvicted(CacheEntriesEvictedEvent<String, ExpiringSession> event) {
        event.getEntries().entrySet().forEach(entry -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Session expired with id: " + entry.getValue().getId());
            }
            eventPublisher.publishEvent(new SessionDeletedEvent(this, entry.getValue()));
        });
    }
    */

    @CacheEntryRemoved
    public void entryRemoved(CacheEntryRemovedEvent<String, ExpiringSession> event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Session expired with id: " + event.getOldValue().getId());
        }
        eventPublisher.publishEvent(new SessionDeletedEvent(this, event.getOldValue()));
    }
}
