package org.littlewings.spring.session.infinispan.embedded.config.annotation.web.http;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(InfinispanEmbeddedHttpSessionConfiguration.class)
@Configuration
public @interface EnableInfinispanEmbeddedHttpSession {
    int maxInactiveIntervalInSeconds() default 1800;

    String sessionsCacheName() default "springSessions";
}
