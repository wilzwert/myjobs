package com.wilzwert.myjobs.core.domain.model;

import java.util.UUID;

public record UserId(UUID value) implements EntityId<UUID> {
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }
}