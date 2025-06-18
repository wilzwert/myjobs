package com.wilzwert.myjobs.core.domain.model.job.event.integration;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:15:56
 */

public final class JobUpdatedEvent extends IntegrationEvent {
    private final JobId jobId;

    public JobUpdatedEvent(IntegrationEventId id, JobId jobId) {
        super(id);
        this.jobId = jobId;
    }

    public JobUpdatedEvent(IntegrationEventId id, Instant occurredAt, JobId jobId) {
        super(id, occurredAt);
        this.jobId = jobId;
    }

    public JobId getJobId() {
        return jobId;
    }
}
