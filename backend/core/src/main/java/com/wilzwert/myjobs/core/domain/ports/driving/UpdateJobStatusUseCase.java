package com.wilzwert.myjobs.core.domain.ports.driving;


import com.wilzwert.myjobs.core.domain.command.UpdateJobStatusCommand;
import com.wilzwert.myjobs.core.domain.model.Job;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:25
 */
public interface UpdateJobStatusUseCase {
    Job updateJobStatus(UpdateJobStatusCommand command);
}
