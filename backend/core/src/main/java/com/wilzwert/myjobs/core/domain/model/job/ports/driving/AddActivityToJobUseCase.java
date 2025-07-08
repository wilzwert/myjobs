package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.command.CreateActivitiesCommand;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface AddActivityToJobUseCase {
    List<Activity> addActivitiesToJob(CreateActivitiesCommand command);
}
