package com.wilzwert.myjobs.infrastructure.persistence.mongo.configuration;

import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.JobRatingReadConverter;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.JobRatingWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;

/**
 * @author Wilhelm Zwertvaegher
 */


@Configuration
@EnableTransactionManagement
public class MongoConfiguration {

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(
                Arrays.asList(
                        new JobRatingReadConverter(),
                        new JobRatingWriteConverter()
                )
        );
    }
}