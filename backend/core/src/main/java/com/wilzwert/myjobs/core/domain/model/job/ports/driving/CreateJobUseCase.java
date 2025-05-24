package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.job.command.CreateJobCommand;
import com.wilzwert.myjobs.core.domain.model.job.Job;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface CreateJobUseCase {
    Job createJob(CreateJobCommand command);
}
