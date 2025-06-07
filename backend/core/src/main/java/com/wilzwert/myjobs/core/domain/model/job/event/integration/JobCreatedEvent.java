package com.wilzwert.myjobs.core.domain.model.job.event.integration;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:15:56
 */

public class JobCreatedEvent extends IntegrationEvent {
    private final JobId jobId;

    public JobCreatedEvent(IntegrationEventId id, JobId jobId) {
        super(id);
        this.jobId = jobId;
    }

    public JobId getJobId() {
        return jobId;
    }
}
