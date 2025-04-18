package com.wilzwert.myjobs.core.domain.ports.driving;


import com.wilzwert.myjobs.core.domain.command.DeleteJobCommand;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:25
 */
public interface DeleteJobUseCase {
    void deleteJob(DeleteJobCommand command);
}
