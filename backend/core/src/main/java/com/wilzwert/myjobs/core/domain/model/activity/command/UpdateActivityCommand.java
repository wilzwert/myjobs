package com.wilzwert.myjobs.core.domain.model.activity.command;

import com.wilzwert.myjobs.core.domain.model.activity.ActivityId;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

/**
 * @author Wilhelm Zwertvaegher
 */

public record UpdateActivityCommand(ActivityId activityId, ActivityType activityType, String comment, UserId userId, JobId jobId) {
}

