package com.wilzwert.myjobs.infrastructure.serialization.jackson;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.serialization.IntegrationEventSerializationHandler;
import com.wilzwert.myjobs.infrastructure.serialization.exception.DeserializationException;
import com.wilzwert.myjobs.infrastructure.serialization.exception.SerializationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wilhelm Zwertvaegher
 * Date:17/06/2025
 * Time:11:56
 */

@Component
@Slf4j
public class JacksonIntegrationEventSerializationHandler implements IntegrationEventSerializationHandler {

    // map used to retrieve actual event classes based on their resolvable type, which in our case will be a simple class name
    private final Map<String, Class<? extends IntegrationEvent>> eventTypeMap = new HashMap<>();

    /**
     * jackson's object mapper with custom configuration
     * @see JacksonConfig
     */
    private final ObjectMapper objectMapper;

    JacksonIntegrationEventSerializationHandler(ObjectMapper objectMapper) {
        for(Class<?> clazz : IntegrationEvent.class.getPermittedSubclasses()) {
            eventTypeMap.put(clazz.getSimpleName(), clazz.asSubclass(IntegrationEvent.class));
        }
        this.objectMapper = objectMapper;
    }

    @Override
    public IntegrationEvent readFromPayload(String type, String payload) throws DeserializationException {
        Class<? extends IntegrationEvent> clazz = eventTypeMap.get(type);
        if (clazz == null) {
            throw new DeserializationException("Unknown event type: " + type);
        }
        try {
            log.info("Reading payload into {}", clazz.getSimpleName());
            return objectMapper.readValue(payload, clazz);
        }
        catch (JsonProcessingException ex) {
            throw new DeserializationException("Cannot process JSON", ex);
        }
    }

    @Override
    public <T extends IntegrationEvent> String serialize(T event)  throws SerializationException {
        try {
            return objectMapper.writeValueAsString(event);
        }
        catch(JsonProcessingException e) {
            throw new SerializationException("Serialization failed", e);
        }
    }
}