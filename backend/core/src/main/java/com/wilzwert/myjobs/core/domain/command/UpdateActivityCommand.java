package com.wilzwert.myjobs.core.domain.command;

import com.wilzwert.myjobs.core.domain.model.ActivityType;
import com.wilzwert.myjobs.core.domain.model.JobId;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record UpdateActivityCommand(JobId jobId, ActivityType activityType, String comment) {
}

