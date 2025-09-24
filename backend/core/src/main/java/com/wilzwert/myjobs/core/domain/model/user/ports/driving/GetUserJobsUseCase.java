package com.wilzwert.myjobs.core.domain.model.user.ports.driving;


import com.wilzwert.myjobs.core.domain.model.job.EnrichedJob;
import com.wilzwert.myjobs.core.domain.model.job.command.GetUserJobsCommand;
import com.wilzwert.myjobs.core.domain.shared.pagination.DomainPage;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface GetUserJobsUseCase {
    DomainPage<EnrichedJob> getUserJobs(GetUserJobsCommand command);
}