package com.wilzwert.myjobs.core.domain.ports.driving;


import com.wilzwert.myjobs.core.application.command.CreateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.Activity;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:25
 */
public interface AddActivityToJobUseCase {
    Activity addActivityToJob(CreateActivityCommand command);
}
