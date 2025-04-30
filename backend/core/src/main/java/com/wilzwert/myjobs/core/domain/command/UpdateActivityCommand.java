package com.wilzwert.myjobs.core.domain.command;

import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.job.JobId;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record UpdateActivityCommand(JobId jobId, ActivityType activityType, String comment) {
}

