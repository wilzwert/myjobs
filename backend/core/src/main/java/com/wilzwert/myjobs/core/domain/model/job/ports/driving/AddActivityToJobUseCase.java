package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.activity.command.CreateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface AddActivityToJobUseCase {
    Activity addActivityToJob(CreateActivityCommand command);
}
