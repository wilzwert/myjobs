package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.job.command.UpdateJobStatusCommand;
import com.wilzwert.myjobs.core.domain.model.job.Job;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface UpdateJobStatusUseCase {
    Job updateJobStatus(UpdateJobStatusCommand command);
}
