package com.wilzwert.myjobs.infrastructure.serialization;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobStatusUpdatedEvent;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            module.addDeserializer(JobStatusUpdatedEvent.class, new JobStatusUpdatedEventDeserializer());
            builder.modules(module);
        };
    }
}
