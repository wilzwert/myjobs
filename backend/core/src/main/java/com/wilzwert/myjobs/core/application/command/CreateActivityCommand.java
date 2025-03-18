package com.wilzwert.myjobs.core.application.command;

import com.wilzwert.myjobs.core.domain.model.ActivityType;
import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.model.UserId;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record CreateActivityCommand(ActivityType activityType, String comment, UserId userId, JobId jobId) {
}

