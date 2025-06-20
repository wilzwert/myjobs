package com.wilzwert.myjobs.infrastructure.staging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoIntegrationEvent;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoRefreshToken;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoUser;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoIntegrationEventRepository;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoJobRepository;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoRefreshTokenRepository;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
@Profile({"integration","staging"})
@Slf4j
public class TestDataLoader {

    private final MongoUserRepository userRepository;
    private final MongoJobRepository jobRepository;
    private final MongoRefreshTokenRepository refreshTokenRepository;
    private final MongoIntegrationEventRepository integrationEventRepository;
    private final ObjectMapper objectMapper;

    public TestDataLoader(MongoUserRepository userRepository, MongoJobRepository jobRepository, MongoRefreshTokenRepository refreshTokenRepository, MongoIntegrationEventRepository integrationEventRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.integrationEventRepository = integrationEventRepository;
        this.objectMapper = objectMapper;
    }

    private <T> void loadData(String fileName, Class<T> clazz, MongoRepository<T, ?> repository) {
        try (InputStream is = getClass().getResourceAsStream("/test-data/" +fileName)) {
            if (is != null) {
                List<T> elements = objectMapper.readValue(is, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
                repository.saveAll(elements);
                log.info("✅ Test data loaded from {} (found {}", fileName, elements.size());
            } else {
                log.error("❌ Cannot find file {} !", fileName);
            }
        } catch (IOException e) {
            log.info("❌ Error while loading test data from {} : {}", fileName, e.getMessage());
        }
    }

    public void resetAndReload() {
        userRepository.deleteAll();
        jobRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        integrationEventRepository.deleteAll();

        loadData("users.json", MongoUser.class, userRepository);
        loadData("jobs.json", MongoJob.class, jobRepository);
        loadData("refresh_token.json", MongoRefreshToken.class, refreshTokenRepository);
        loadData("integration_events.json", MongoIntegrationEvent.class, integrationEventRepository);
    }

    @Bean
    public CommandLineRunner loadTestData() {
        return args -> resetAndReload();
    }
}
