package com.wilzwert.myjobs.infrastructure.batch;


import org.springframework.batch.core.configuration.annotation.EnableMongoJobRepository;
import org.springframework.context.annotation.Configuration;


/**
 * Configure Spring Batch to use mongo repo and explorer
 * @author Wilhelm Zwertvaegher
 */
@Configuration
@EnableMongoJobRepository
public class BatchConfiguration {
}
