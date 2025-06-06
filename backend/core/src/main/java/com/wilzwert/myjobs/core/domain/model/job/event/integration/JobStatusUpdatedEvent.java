package com.wilzwert.myjobs.core.domain.model.job.event.integration;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:15:56
 */

public class JobStatusUpdatedEvent extends IntegrationEvent {
    private final JobId jobId;

    private final JobStatus jobStatus;

    public JobStatusUpdatedEvent(JobId jobId, JobStatus jobStatus) {
        super();
        this.jobId = jobId;
        this.jobStatus = jobStatus;
    }

    public JobId getJobId() {
        return jobId;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }
}
