package com.wilzwert.myjobs.infrastructure.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoJobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
@Profile({"integration"})
public class TestDataLoader {

    private final MongoJobRepository jobRepository;
    private final ObjectMapper objectMapper;

    public TestDataLoader(MongoJobRepository jobRepository, ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.objectMapper = objectMapper;
    }

    @Bean
    public CommandLineRunner loadTestData() {
        System.out.println("loading test data");
        return args -> {
            try (InputStream is = getClass().getResourceAsStream("/test-data/jobs.json")) {
                if (is != null) {
                    List<MongoJob> jobs = objectMapper.readValue(is, new TypeReference<List<MongoJob>>() {});
                    jobRepository.saveAll(jobs);
                    System.out.println("✅ Test data loaded");
                } else {
                    System.err.println("❌ Cannot find file jobs.json !");
                }
            } catch (IOException e) {
                System.err.println("❌ Error while loading test data : " + e.getMessage());
            }
        };
    }
}
