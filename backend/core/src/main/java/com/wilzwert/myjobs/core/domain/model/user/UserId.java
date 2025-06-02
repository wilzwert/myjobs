package com.wilzwert.myjobs.core.domain.model.user;

import com.wilzwert.myjobs.core.domain.model.EntityId;

import java.io.Serializable;
import java.util.UUID;

public record UserId(UUID value) implements Serializable, EntityId<UUID> {
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}