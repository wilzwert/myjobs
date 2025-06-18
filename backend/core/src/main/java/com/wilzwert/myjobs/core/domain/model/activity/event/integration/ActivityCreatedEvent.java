package com.wilzwert.myjobs.core.domain.model.activity.event.integration;


import com.wilzwert.myjobs.core.domain.model.activity.ActivityId;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:15:56
 */

public final class ActivityCreatedEvent extends IntegrationEvent {
    private final JobId jobId;
    private final ActivityId activityId;
    private final ActivityType activityType;

    public ActivityCreatedEvent(IntegrationEventId id, JobId jobId, ActivityId activityId, ActivityType activityType) {
        super(id);
        this.jobId = jobId;
        this.activityId = activityId;
        this.activityType = activityType;
    }

    public ActivityCreatedEvent(IntegrationEventId id, Instant occurredAt, JobId jobId, ActivityId activityId, ActivityType activityType) {
        super(id, occurredAt);
        this.jobId = jobId;
        this.activityId = activityId;
        this.activityType = activityType;
    }

    public JobId getJobId() {
        return jobId;
    }

    public ActivityId getActivityId() {
        return activityId;
    }

    public ActivityType getActivityType() {
        return activityType;
    }
}