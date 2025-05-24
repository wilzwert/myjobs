package com.wilzwert.myjobs.core.domain.model.job.command;


import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import com.wilzwert.myjobs.core.domain.model.user.UserId;


/**
 * @author Wilhelm Zwertvaegher
 */

public record UpdateJobRatingCommand(JobId jobId, UserId userId, JobRating rating) {
}
