package com.wilzwert.myjobs.core.domain.command;

import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

import java.io.File;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record CreateAttachmentCommand(String name, File file, String filename, UserId userId, JobId jobId) {
}

