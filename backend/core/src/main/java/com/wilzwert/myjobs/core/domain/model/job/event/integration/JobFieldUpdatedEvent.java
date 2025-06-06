package com.wilzwert.myjobs.core.domain.model.job.event.integration;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.command.UpdateJobFieldCommand;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:15:56
 */

public class JobFieldUpdatedEvent extends IntegrationEvent {

    private final JobId jobId;

    private final UpdateJobFieldCommand.Field field;

    public JobFieldUpdatedEvent(JobId jobId, UpdateJobFieldCommand.Field field) {
        super();
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
