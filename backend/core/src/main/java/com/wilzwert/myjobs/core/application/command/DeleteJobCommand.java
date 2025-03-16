package com.wilzwert.myjobs.core.application.command;


import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.model.UserId;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record DeleteJobCommand(JobId jobId, UserId userId) {
}



