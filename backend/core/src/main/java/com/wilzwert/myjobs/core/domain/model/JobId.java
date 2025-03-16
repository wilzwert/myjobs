package com.wilzwert.myjobs.core.domain.model;

import java.util.UUID;

public record JobId(UUID value) implements EntityId<UUID> {
    public static JobId  generate() {
        return new JobId(UUID.randomUUID());
    }
}
