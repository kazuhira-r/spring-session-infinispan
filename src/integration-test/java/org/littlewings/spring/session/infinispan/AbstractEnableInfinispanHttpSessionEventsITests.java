package org.littlewings.spring.session.infinispan;

import java.util.EventObject;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.ExpiringSession;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.SessionEventRegistry;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionExpiredEvent;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractEnableInfinispanHttpSessionEventsITests<S extends ExpiringSession> {
    protected static final String SESSION_CACHE_NAME = "sessionCache";
    protected static final int MAX_INACTIVE_INTERVAL_IN_SECONDS = 1;

    @Autowired
    private SessionRepository<S> repository;

    @Autowired
    private SessionEventRegistry registry;

    @Before
    public void setup() {
        registry.clear();
    }

    @Test
    public void saveSessionTest() throws InterruptedException {
        String username = "saves-" + System.currentTimeMillis();

        S sessionToSave = repository.createSession();

        String expectedAttributeName = "a";
        String expectedAttributeValue = "b";
        sessionToSave.setAttribute(expectedAttributeName, expectedAttributeValue);
        Authentication toSaveToken = new UsernamePasswordAuthenticationToken(username,
                "password", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContext toSaveContext = SecurityContextHolder.createEmptyContext();
        toSaveContext.setAuthentication(toSaveToken);
        sessionToSave.setAttribute("SPRING_SECURITY_CONTEXT", toSaveContext);
        sessionToSave.setAttribute(
                FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, username);

        repository.save(sessionToSave);

        assertThat(registry.receivedEvent(sessionToSave.getId())).isTrue();
        assertThat((EventObject) registry.getEvent(sessionToSave.getId()))
                .isInstanceOf(SessionCreatedEvent.class);

        Session session = repository.getSession(sessionToSave.getId());

        assertThat(session.getId()).isEqualTo(sessionToSave.getId());
        assertThat(session.getAttributeNames())
                .isEqualTo(sessionToSave.getAttributeNames());
        assertThat((String) session.getAttribute(expectedAttributeName))
                .isEqualTo(sessionToSave.getAttribute(expectedAttributeName));
    }

    @Test
    public void expiredSessionTest() throws InterruptedException {
        S sessionToSave = repository.createSession();

        repository.save(sessionToSave);

        assertThat(registry.receivedEvent(sessionToSave.getId())).isTrue();
        assertThat((EventObject) registry.getEvent(sessionToSave.getId()))
                .isInstanceOf(SessionCreatedEvent.class);

        registry.clear();

        assertThat(sessionToSave.getMaxInactiveIntervalInSeconds())
                .isEqualTo(MAX_INACTIVE_INTERVAL_IN_SECONDS);

        assertThat(registry.receivedEvent(sessionToSave.getId())).isTrue();
        assertThat((EventObject) registry.getEvent(sessionToSave.getId()))
                .isInstanceOf(SessionExpiredEvent.class);

        assertThat(repository.getSession(sessionToSave.getId())).isNull();
    }

    @Test
    public void deletedSessionTest() throws InterruptedException {
        S sessionToSave = repository.createSession();

        repository.save(sessionToSave);

        assertThat(registry.receivedEvent(sessionToSave.getId())).isTrue();
        assertThat((EventObject) registry.getEvent(sessionToSave.getId()))
                .isInstanceOf(SessionCreatedEvent.class);

        registry.clear();

        repository.delete(sessionToSave.getId());

        assertThat(registry.receivedEvent(sessionToSave.getId())).isTrue();
        assertThat((EventObject) registry.getEvent(sessionToSave.getId()))
                .isInstanceOf(SessionDeletedEvent.class);

        assertThat(repository.getSession(sessionToSave.getId())).isNull();
    }

    @Test
    public void saveUpdatesTimeToLiveTest() throws InterruptedException {
        Object lock = new Object();

        S sessionToSave = repository.createSession();

        repository.save(sessionToSave);

        synchronized (lock) {
            lock.wait((sessionToSave.getMaxInactiveIntervalInSeconds() * 1000) - 500);
        }

        // Get and save the session like SessionRepositoryFilter would.
        S sessionToUpdate = repository.getSession(sessionToSave.getId());
        sessionToUpdate.setLastAccessedTime(System.currentTimeMillis());
        repository.save(sessionToUpdate);

        synchronized (lock) {
            lock.wait((sessionToUpdate.getMaxInactiveIntervalInSeconds() * 1000) - 100);
        }

        assertThat(repository.getSession(sessionToUpdate.getId())).isNotNull();
    }
}
