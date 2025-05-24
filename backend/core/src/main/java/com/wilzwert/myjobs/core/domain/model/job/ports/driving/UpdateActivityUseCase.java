package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.command.UpdateActivityCommand;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface UpdateActivityUseCase {
    Activity updateActivity(UpdateActivityCommand command);
}
