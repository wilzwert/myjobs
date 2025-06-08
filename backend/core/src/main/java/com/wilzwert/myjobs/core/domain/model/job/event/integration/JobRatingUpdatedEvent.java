package com.wilzwert.myjobs.core.domain.model.job.event.integration;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/06/2025
 * Time:15:56
 */

public class JobRatingUpdatedEvent extends IntegrationEvent {
    private final JobId jobId;

    private final JobRating jobRating;

    public JobRatingUpdatedEvent(IntegrationEventId id, JobId jobId, JobRating jobRating) {
        super(id);
        this.jobId = jobId;
        this.jobRating = jobRating;
    }

    public JobRatingUpdatedEvent(IntegrationEventId id, Instant occurredAt, JobId jobId, JobRating jobRating) {
        super(id, occurredAt);
        this.jobId = jobId;
        this.jobRating = jobRating;
    }

    public JobId getJobId() {
        return jobId;
    }

    public JobRating getJobRating() {
        return jobRating;
    }
}
