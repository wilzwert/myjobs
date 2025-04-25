package com.wilzwert.myjobs.core.domain.model.activity;

import com.wilzwert.myjobs.core.domain.model.EntityId;

import java.util.UUID;

public record ActivityId(UUID value) implements EntityId<UUID> {
    public static ActivityId generate() {
        return new ActivityId(UUID.randomUUID());
    }
}
