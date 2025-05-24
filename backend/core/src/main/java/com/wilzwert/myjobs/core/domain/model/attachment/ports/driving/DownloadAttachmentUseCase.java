package com.wilzwert.myjobs.core.domain.model.attachment.ports.driving;


import com.wilzwert.myjobs.core.domain.model.attachment.command.DownloadAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.DownloadableFile;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface DownloadAttachmentUseCase {
    DownloadableFile downloadAttachment(DownloadAttachmentCommand command);
}
