package com.wilzwert.myjobs.infrastructure.serialization.jackson;


import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;

/**
 * @author Wilhelm Zwertvaegher
 * Date:17/06/2025
 * Time:14:41
 */

public abstract class JacksonIntegrationEventDeserializer<T extends IntegrationEvent> extends StdDeserializer<T> {

    private final Class<T> eventClass;

    protected JacksonIntegrationEventDeserializer(Class<T> vc) {
        super(vc);
        this.eventClass = vc;
    }

    public final Class<T> getEventClass() {
        return eventClass;
    }
}