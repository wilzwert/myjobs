package com.wilzwert.myjobs.infrastructure.serialization;


import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.serialization.exception.DeserializationException;
import com.wilzwert.myjobs.infrastructure.serialization.exception.SerializationException;

/**
 * @author Wilhelm Zwertvaegher
 * Date:17/06/2025
 * Time:11:56
 */
public interface IntegrationEventSerializationHandler {

    /**
     *
     * @param event the event
     * @return a string representing a resolvable class name which could be used to deserialize the event later
     * That name matches the eventTypeMap key
     */
    default <T extends IntegrationEvent> String getResolvableType(T event) {
        return event.getClass().getSimpleName();
    }

    IntegrationEvent readFromPayload(String type, String payload) throws DeserializationException;

    <T extends IntegrationEvent> String serialize(T event) throws SerializationException;
}