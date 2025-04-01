package com.wilzwert.myjobs.core.domain.command;


import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.model.JobRating;
import com.wilzwert.myjobs.core.domain.model.JobStatus;
import com.wilzwert.myjobs.core.domain.model.UserId;


/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record UpdateJobRatingCommand(JobId jobId, UserId userId, JobRating rating) {
}
