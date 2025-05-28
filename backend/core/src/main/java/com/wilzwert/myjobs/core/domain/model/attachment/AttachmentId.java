package com.wilzwert.myjobs.core.domain.model.attachment;

import com.wilzwert.myjobs.core.domain.model.EntityId;

import java.util.UUID;

public record AttachmentId(UUID value) implements EntityId<UUID> {
    public static AttachmentId generate() {
        return new AttachmentId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
