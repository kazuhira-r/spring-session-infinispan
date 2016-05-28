package org.littlewings.spring.session.infinispan.remote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryExpired;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryRemoved;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.event.ClientCacheEntryCreatedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryExpiredEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryRemovedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.session.ExpiringSession;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionExpiredEvent;
import org.springframework.util.Assert;

// Hot Rod ClientListener, expired event not supported.
@ClientListener
public class RemoteSessionEntryListener {
    private Log logger = LogFactory.getLog(RemoteSessionEntryListener.class);

    private ApplicationEventPublisher eventPublisher;

    public RemoteSessionEntryListener(ApplicationEventPublisher eventPublisher) {
        Assert.notNull(eventPublisher, "eventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }

    @ClientCacheEntryCreated
    public void entryCreated(ClientCacheEntryCreatedEvent<String> event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Session created with id: " + event.getKey());
        }
        eventPublisher.publishEvent(new SessionCreatedEvent(this, event.getKey()));
    }

    @ClientCacheEntryExpired
    public void entryExpired(ClientCacheEntryExpiredEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Session expired with id: " + event.getKey());
        }

        eventPublisher.publishEvent(new SessionExpiredEvent(this, (String)event.getKey()));
    }

    @ClientCacheEntryRemoved
    public void entryRemoved(ClientCacheEntryRemovedEvent<String> event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Session expired with id: " + event.getKey());
        }
        eventPublisher.publishEvent(new SessionDeletedEvent(this, event.getKey()));
    }
}
