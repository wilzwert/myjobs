package com.wilzwert.myjobs.core.domain.ports.driving;


import com.wilzwert.myjobs.core.domain.model.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:25
 */
public interface GetUserJobsUseCase {
    DomainPage<Job> getUserJobs(UserId userId, int page, int size, JobStatus status, String sort);
}
