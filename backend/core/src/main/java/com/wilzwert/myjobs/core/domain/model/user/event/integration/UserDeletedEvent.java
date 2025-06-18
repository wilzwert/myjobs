package com.wilzwert.myjobs.core.domain.model.user.event.integration;


import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:15:56
 */

public final class UserDeletedEvent extends IntegrationEvent {
    private final UserId userId;

    public UserDeletedEvent(IntegrationEventId id, UserId userId) {
        super(id);
        this.userId = userId;
    }

    public UserDeletedEvent(IntegrationEventId id, Instant occurredAt, UserId userId) {
        super(id, occurredAt);
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }
}
