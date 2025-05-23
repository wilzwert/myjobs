package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;

import com.wilzwert.myjobs.core.domain.model.*;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityId;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface IdMapper {
    default UUID toEntity(EntityId<UUID> entityId) {
        return entityId.value();
    }

    default UserId mapUserId(UUID id) {
        return new UserId(id);
    }

    default JobId mapJobId(UUID id) {
        return new JobId(id);
    }

    default ActivityId mapActivityId(UUID id) {
        return new ActivityId(id);
    }

    default AttachmentId mapAttachmentId(UUID id) {
        return new AttachmentId(id);
    }
}
