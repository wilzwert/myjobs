package com.wilzwert.myjobs.domain.ports.driving;


import com.wilzwert.myjobs.domain.command.DeleteJobCommand;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:25
 */
public interface DeleteJobUseCase {
    void deleteJob(DeleteJobCommand command);
}
