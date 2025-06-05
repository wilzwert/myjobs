package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.job.command.UpdateJobFullCommand;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.command.UpdateJobFieldCommand;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface UpdateJobUseCase {
    Job updateJob(UpdateJobFullCommand command);

    Job updateJobField(UpdateJobFieldCommand command);
}
