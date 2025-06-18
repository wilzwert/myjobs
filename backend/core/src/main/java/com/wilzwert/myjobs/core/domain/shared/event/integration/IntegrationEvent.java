package com.wilzwert.myjobs.core.domain.shared.event.integration;


import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityAutomaticallyCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.attachment.event.integration.AttachmentCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.attachment.event.integration.AttachmentDeletedEvent;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.*;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserDeletedEvent;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserUpdatedEvent;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:15:53
 */

public sealed abstract class IntegrationEvent permits
        ActivityAutomaticallyCreatedEvent, ActivityCreatedEvent,
        AttachmentCreatedEvent, AttachmentDeletedEvent,
        JobCreatedEvent, JobUpdatedEvent, JobDeletedEvent,
        JobFieldUpdatedEvent,
        JobRatingUpdatedEvent,
        JobStatusUpdatedEvent,
        UserCreatedEvent, UserDeletedEvent, UserUpdatedEvent {
    private final IntegrationEventId id;

    private final Instant occurredAt;

    protected IntegrationEvent(IntegrationEventId id, Instant occurredAt) {
        this.id = id;
        this.occurredAt = occurredAt;
    }

    protected IntegrationEvent(IntegrationEventId id) {
        this(id, Instant.now());
    }

    public IntegrationEventId getId() {
        return id;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
