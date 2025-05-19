package com.wilzwert.myjobs.core.domain.model.attachment.ports.driving;


import com.wilzwert.myjobs.core.domain.model.attachment.command.DownloadAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.DownloadableFile;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:25
 */
public interface DownloadAttachmentUseCase {
    DownloadableFile downloadAttachment(DownloadAttachmentCommand command);
}
