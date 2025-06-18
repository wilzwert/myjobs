package com.wilzwert.myjobs.core.domain.shared.event.integration;

import com.wilzwert.myjobs.core.domain.model.EntityId;

import java.util.UUID;

public record IntegrationEventId(UUID value) implements EntityId<UUID> {
    public static IntegrationEventId generate() {
        return new IntegrationEventId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
