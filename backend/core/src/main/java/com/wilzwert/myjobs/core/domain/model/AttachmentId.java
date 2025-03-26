package com.wilzwert.myjobs.core.domain.model;

import java.util.UUID;

public record AttachmentId(UUID value) implements EntityId<UUID> {
    public static AttachmentId generate() {
        return new AttachmentId(UUID.randomUUID());
    }
}
