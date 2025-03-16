package com.wilzwert.myjobs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.wilzwert.myjobs.infrastructure.persistence.mongo.repository")
@EnableConfigurationProperties
public class MyJobsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyJobsApplication.class, args);
    }
}
