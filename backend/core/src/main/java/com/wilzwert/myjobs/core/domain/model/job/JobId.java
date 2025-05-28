package com.wilzwert.myjobs.core.domain.model.job;

import com.wilzwert.myjobs.core.domain.model.EntityId;

import java.util.UUID;

public record JobId(UUID value) implements EntityId<UUID> {
    public static JobId  generate() {
        return new JobId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
