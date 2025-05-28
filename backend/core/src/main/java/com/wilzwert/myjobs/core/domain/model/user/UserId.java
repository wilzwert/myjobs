package com.wilzwert.myjobs.core.domain.model.user;

import com.wilzwert.myjobs.core.domain.model.EntityId;

import java.util.UUID;

public record UserId(UUID value) implements EntityId<UUID> {
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}