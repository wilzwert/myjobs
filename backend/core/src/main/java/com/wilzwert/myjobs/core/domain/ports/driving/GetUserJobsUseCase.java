package com.wilzwert.myjobs.core.domain.ports.driving;


import com.wilzwert.myjobs.core.domain.model.DomainPage;
import com.wilzwert.myjobs.core.domain.model.Job;
import com.wilzwert.myjobs.core.domain.model.JobStatus;
import com.wilzwert.myjobs.core.domain.model.UserId;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:25
 */
public interface GetUserJobsUseCase {
    DomainPage<Job> getUserJobs(UserId userId, int page, int size, JobStatus status);
}
