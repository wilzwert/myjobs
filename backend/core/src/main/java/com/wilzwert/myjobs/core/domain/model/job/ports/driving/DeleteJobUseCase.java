package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.job.command.DeleteJobCommand;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface DeleteJobUseCase {
    void deleteJob(DeleteJobCommand command);
}
