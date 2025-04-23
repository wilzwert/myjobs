package com.wilzwert.myjobs.infrastructure.persistence.mongo.configuration;

import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.JobRatingReadConverter;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.JobRatingWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

/**
 * @author Wilhelm Zwertvaegher
 * Date:22/04/2025
 * Time:16:05
 */


@Configuration
public class MongoConfiguration {

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