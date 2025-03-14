package com.wilzwert.myapps.domain.ports.driving;


import com.wilzwert.myapps.domain.command.CreateJobCommand;
import com.wilzwert.myapps.domain.model.Job;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:25
 */
public interface CreateJobUseCase {
    Job createJob(CreateJobCommand command);
}
