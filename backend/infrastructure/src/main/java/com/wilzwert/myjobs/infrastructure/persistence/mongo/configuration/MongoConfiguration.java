package com.wilzwert.myjobs.infrastructure.persistence.mongo.configuration;

import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.JobRatingReadConverter;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.JobRatingWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Arrays;

/**
 * @author Wilhelm Zwertvaegher
 */


@Configuration
public class MongoConfiguration {

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
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

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory,
                                                       MongoCustomConversions conversions,
                                                       MongoMappingContext context) {

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, context);
        converter.setCustomConversions(conversions);

        converter.setMapKeyDotReplacement("_");
        return converter;
    }
}