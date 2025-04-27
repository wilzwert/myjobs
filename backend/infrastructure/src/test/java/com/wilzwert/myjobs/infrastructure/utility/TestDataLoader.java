package com.wilzwert.myjobs.infrastructure.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoUser;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoJobRepository;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
@Profile({"integration"})
public class TestDataLoader {

    private final MongoUserRepository userRepository;
    private final MongoJobRepository jobRepository;
    private final ObjectMapper objectMapper;

    public TestDataLoader(MongoUserRepository userRepository, MongoJobRepository jobRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.objectMapper = objectMapper;
    }

    private <T> void loadData(String fileName, Class<T> clazz, MongoRepository<T, ?> repository) {
        try (InputStream is = getClass().getResourceAsStream("/test-data/"+fileName)) {
            if (is != null) {
                List<T> elements = objectMapper.readValue(is, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
                repository.saveAll(elements);
                System.out.println("✅ Test data loaded from "+fileName+" (found "+elements.size()+")" );
            } else {
                System.err.println("❌ Cannot find file "+fileName+" !");
            }
        } catch (IOException e) {
            System.err.println("❌ Error while loading test data from "+fileName+" : " + e.getMessage());
        }
    }

    @Bean
    public CommandLineRunner loadTestData() {
        System.out.println("loading test data");
        return args -> {
            loadData("users.json", MongoUser.class, userRepository);
            loadData("jobs.json", MongoJob.class, jobRepository);
        };
    }
}
