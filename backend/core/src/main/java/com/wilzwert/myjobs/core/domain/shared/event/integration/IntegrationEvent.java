package com.wilzwert.myjobs.core.domain.shared.event.integration;


import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:15:53
 */

public abstract class IntegrationEvent {
    private final Instant occurredAt;

    public IntegrationEvent() {
        this.occurredAt = Instant.now();
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
