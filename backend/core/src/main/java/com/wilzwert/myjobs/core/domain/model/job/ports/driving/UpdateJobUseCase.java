package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.job.command.UpdateJobCommand;
import com.wilzwert.myjobs.core.domain.model.job.Job;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface UpdateJobUseCase {
    Job updateJob(UpdateJobCommand command);
}
