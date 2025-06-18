package com.wilzwert.myjobs.core.domain.model.job.event.integration;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.command.UpdateJobFieldCommand;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:15:56
 */

public final class JobFieldUpdatedEvent extends IntegrationEvent {

    private final JobId jobId;

    private final UpdateJobFieldCommand.Field field;

    public JobFieldUpdatedEvent(IntegrationEventId id, JobId jobId, UpdateJobFieldCommand.Field field) {
        super(id);
        this.jobId = jobId;
        this.field = field;
    }

    public JobFieldUpdatedEvent(IntegrationEventId id, Instant occurredAt, JobId jobId, UpdateJobFieldCommand.Field field) {
        super(id, occurredAt);
        this.jobId = jobId;
        this.field = field;
    }

    public JobId getJobId() {
        return jobId;
    }

    public UpdateJobFieldCommand.Field getField() {
        return field;
    }
}
