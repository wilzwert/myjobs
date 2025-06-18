package com.wilzwert.myjobs.infrastructure.configuration;


import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoIntegrationEventRepository;
import com.wilzwert.myjobs.infrastructure.utility.IntegrationEventUtility;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author Wilhelm Zwertvaegher
 * Date:18/06/2025
 * Time:11:11
 */

@TestConfiguration
public class IntegrationTestConfig {
    @Bean
    public IntegrationEventUtility integrationEventUtility(MongoIntegrationEventRepository repository) {
        return new IntegrationEventUtility(repository);
    }
}
