package com.wilzwert.myjobs.core.domain.command;

import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.model.UserId;

import java.io.File;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record DownloadAttachmentCommand(String id, UserId userId, JobId jobId) {
}

