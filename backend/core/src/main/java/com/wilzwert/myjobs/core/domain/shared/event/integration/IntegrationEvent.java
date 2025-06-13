package com.wilzwert.myjobs.core.domain.shared.event.integration;


import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:15:53
 */

public abstract class IntegrationEvent {
    private final IntegrationEventId id;

    private final Instant occurredAt;

    protected IntegrationEvent(IntegrationEventId id, Instant occurredAt) {
        this.id = id;
        this.occurredAt = occurredAt;
    }

    protected IntegrationEvent(IntegrationEventId id) {
        this(id, Instant.now());
    }

    public IntegrationEventId getId() {
        return id;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
