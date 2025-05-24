package com.wilzwert.myjobs.core.domain.model.attachment.command;

import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

/**
 * @author Wilhelm Zwertvaegher
 */

public record DownloadAttachmentCommand(String id, UserId userId, JobId jobId) {
}

