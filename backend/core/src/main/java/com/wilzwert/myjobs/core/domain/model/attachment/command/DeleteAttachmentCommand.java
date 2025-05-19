package com.wilzwert.myjobs.core.domain.model.attachment.command;

import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record DeleteAttachmentCommand(AttachmentId id, UserId userId, JobId jobId) {
}

