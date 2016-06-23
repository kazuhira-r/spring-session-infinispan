# Spring Session Infinispan [![Build Status](https://travis-ci.org/kazuhira-r/spring-session-infinispan.svg?branch=master)](https://travis-ci.org/kazuhira-r/spring-session-infinispan)
Spring Session implementation for Infinispan.

## [Spring Session](http://projects.spring.io/spring-session/)
API and implementations for managing a user’s session information.

## [Infinispan](http://infinispan.org/)
Open Source In Memory Data Grid.

### Support Mode
* Embedded Mode (a.k.a Library Mode)
* Client/Server Mode (Hot Rod Client)

## Configuration
* maxInactiveIntervalInSeconds - session timeout seconds (default: 1800 sec)
* sessionsCacheName - sessions store cache name (default: springSessions)

## Usage
### Build & Install
git clone & Maven build.
```sh
$ git clone https://github.com/kazuhira-r/spring-session-infinispan.git
$ cd spring-session-infinispan
$ mvn install -DskipTests=true
```

Add dependency, this module.
```xml
        <dependency>
            <groupId>org.littlewings</groupId>
            <artifactId>spring-session-infinispan</artifactId>
            <version>${spring.session.infinispan.version}</version>
        </dependency>
```

### Embedded Mode
Add dependency, Infinispan Core module.
```xml
        <dependency>
            <groupId>org.infinispan</groupId>
            <artifactId>infinispan-core</artifactId>
            <version>${infinispan.version}</version>
        </dependency>
```

Enable, Spring Session Infinispan for Embedded Mode.
```java
@SpringBootApplication
@EnableInfinispanEmbeddedHttpSession
public class App {
```

Write a Bean definition of EmbeddedCacheManager.
```java
    @Bean
    public EmbeddedCacheManager embeddedCacheManager() {
        EmbeddedCacheManager cacheManager =
                new DefaultCacheManager(new GlobalConfigurationBuilder().transport().defaultTransport().build());
        cacheManager
                .defineConfiguration("springSessions",
                        new org.infinispan.configuration.cache.ConfigurationBuilder().clustering().cacheMode(CacheMode.DIST_SYNC).build());

        return cacheManager;
    }
```

### Client/Server Mode
*Note: It's necessary to define Cache on the server side beforehand in case of Client/Server Mode.*

Add depencency, Infinispan Client Hot Rod module.
```xml
        <dependency>
            <groupId>org.infinispan</groupId>
            <artifactId>infinispan-client-hotrod</artifactId>
            <version>${infinispan.version}</version>
        </dependency>
```

Enable, Spring Session Infinispan for Client/Server Mode.
```java
@SpringBootApplication
@EnableInfinispanRemoteHttpSession
public class App {
```

Write a Bean definition of RemoteCacheManager.
```java
    @Bean
    public RemoteCacheManager remoteCacheManager() {
        return new RemoteCacheManager(
                new org.infinispan.client.hotrod.configuration.ConfigurationBuilder()
                        .addServers("localhost:11222")
                        .build());
    }
```
