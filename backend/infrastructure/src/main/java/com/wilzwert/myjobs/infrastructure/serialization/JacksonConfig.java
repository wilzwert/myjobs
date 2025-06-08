package com.wilzwert.myjobs.infrastructure.serialization;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.*;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Wilhelm Zwertvaegher
 * Domain integration events are stored as json payloads in mongodb
 * We need some custom logic to deserialize this json representation into concrete integration events
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            module.addDeserializer(JobCreatedEvent.class, new JobCreatedEventDeserializer());
            module.addDeserializer(JobUpdatedEvent.class, new JobUpdatedEventDeserializer());
            module.addDeserializer(JobStatusUpdatedEvent.class, new JobStatusUpdatedEventDeserializer());
            module.addDeserializer(JobRatingUpdatedEvent.class, new JobRatingUpdatedEventDeserializer());
            module.addDeserializer(JobFieldUpdatedEvent.class, new JobFieldUpdatedEventDeserializer());
            builder.modulesToInstall(module);
        };
    }
}