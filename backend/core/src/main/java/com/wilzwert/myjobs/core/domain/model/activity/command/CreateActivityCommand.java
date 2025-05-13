package com.wilzwert.myjobs.core.domain.model.activity.command;

import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record CreateActivityCommand(ActivityType activityType, String comment, UserId userId, JobId jobId) {
}

