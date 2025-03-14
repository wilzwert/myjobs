package com.wilzwert.myapps.infrastructure.configuration;


import org.springframework.context.annotation.Bean;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:21
 */
/*
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    @Bean
    MongoDBContainer mongoDbContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:8"));
    }

    @Bean
    DynamicPropertyRegistrar dynamicPropertyRegistrar(MongoDBContainer mongoDbContainer) {
        return registry -> {
            registry.add("spring.data.mongodb.uri", mongoDbContainer::getConnectionString);
            registry.add("spring.data.mongodb.database", () -> "technical-content-management");
        };
    }
}*/
