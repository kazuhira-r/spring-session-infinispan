package org.littlewings.spring.session.infinispan;

import org.assertj.core.util.Compatibility;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.ExpiringSession;
import org.springframework.session.SessionRepository;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractInfinispanHttpSessionConfigurationXmlITests {
    public static abstract class AbstractCustomXmlCacheNameTest<S extends ExpiringSession> {
        @Autowired
        private SessionRepository<S> repository;

        @Test
        public void saveSessionTest() throws InterruptedException {
            S sessionToSave = repository.createSession();

            repository.save(sessionToSave);

            S session = repository.getSession(sessionToSave.getId());

            assertThat(session.getId()).isEqualTo(sessionToSave.getId());
            assertThat(session.getMaxInactiveIntervalInSeconds()).isEqualTo(1800);
        }
    }

    public static abstract class AbstractCustomXmlCacheNameAndIdleTest<S extends ExpiringSession> {
        @Autowired
        private SessionRepository<S> repository;

        @Test
        public void saveSessionTest() throws InterruptedException {
            S sessionToSave = repository.createSession();

            repository.save(sessionToSave);

            S session = repository.getSession(sessionToSave.getId());

            assertThat(session.getId()).isEqualTo(sessionToSave.getId());
            assertThat(session.getMaxInactiveIntervalInSeconds()).isEqualTo(1200);
        }
    }
}
