package com.wilzwert.myjobs.core.domain.model.user.ports.driving;


import com.wilzwert.myjobs.core.domain.model.job.EnrichedJob;
import com.wilzwert.myjobs.core.domain.model.job.JobStatusFilter;
import com.wilzwert.myjobs.core.domain.shared.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface GetUserJobsUseCase {
    DomainPage<EnrichedJob> getUserJobs(UserId userId, int page, int size, JobStatus status, JobStatusFilter statusFilter, String sort);
}
