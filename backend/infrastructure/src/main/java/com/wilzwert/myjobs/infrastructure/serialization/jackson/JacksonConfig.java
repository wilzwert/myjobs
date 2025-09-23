package com.wilzwert.myjobs.infrastructure.serialization.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Domain integration events are stored as json payloads in mongodb
 * We need some custom logic to deserialize this json representation into concrete integration events
 * For now we are using json with jackson
 */
@Configuration
public class JacksonConfig {
    /**
     * List of custom deserializers implementing {@link JacksonIntegrationEventDeserializer}
     * The list is automatically built by Spring because all deserializer MUST be beans (@Component)
     * Then we inject the list in this config class to register the deserializers and add them to jackson
     */
    private final List<JacksonIntegrationEventDeserializer<? extends IntegrationEvent>> deserializers;

    public JacksonConfig(List<JacksonIntegrationEventDeserializer<? extends IntegrationEvent>> deserializers) {
        this.deserializers = deserializers;
    }

    private <T extends IntegrationEvent> void registerDeserializer(SimpleModule module, JacksonIntegrationEventDeserializer<T> deserializer) {
        Class<T> clazz = deserializer.getEventClass();
        module.addDeserializer(clazz, deserializer);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            for (JacksonIntegrationEventDeserializer<? extends IntegrationEvent> deserializer : deserializers) {
                Class<? extends IntegrationEvent> targetClass = deserializer.getEventClass();
                if (targetClass == null) {
                    throw new IllegalStateException("handledType == null for " + deserializer);
                }
                registerDeserializer(module, deserializer);
            }
            builder.modulesToInstall(module);
        };
    }
}