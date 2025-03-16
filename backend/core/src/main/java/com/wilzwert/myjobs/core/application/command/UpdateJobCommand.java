package com.wilzwert.myjobs.core.application.command;


import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.model.JobStatus;
import com.wilzwert.myjobs.core.domain.model.UserId;


/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record UpdateJobCommand(JobId jobId, UserId userId, JobStatus status, String title, String url, String description, String profile) {
}
