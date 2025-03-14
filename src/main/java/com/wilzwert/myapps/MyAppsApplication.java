package com.wilzwert.myapps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.wilzwert.myapps.infrastructure.persistence.mongo.repository")
@EnableConfigurationProperties
public class MyAppsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyAppsApplication.class, args);
    }

}
