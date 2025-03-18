package com.wilzwert.myjobs.core.domain.model;

import java.util.UUID;

public record ActivityId(UUID value) implements EntityId<UUID> {
    public static ActivityId generate() {
        return new ActivityId(UUID.randomUUID());
    }
}
