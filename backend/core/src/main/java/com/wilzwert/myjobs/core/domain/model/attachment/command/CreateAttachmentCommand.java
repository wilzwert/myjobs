package com.wilzwert.myjobs.core.domain.model.attachment.command;

import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

import java.io.File;

/**
 * @author Wilhelm Zwertvaegher
 */

public record CreateAttachmentCommand(String name, File file, String filename, UserId userId, JobId jobId) {
}

