package com.wilzwert.myjobs.core.domain.ports.driving;


import com.wilzwert.myjobs.core.domain.command.CreateJobCommand;
import com.wilzwert.myjobs.core.domain.model.job.Job;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:25
 */
public interface CreateJobUseCase {
    Job createJob(CreateJobCommand command);
}
