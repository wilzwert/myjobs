package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.attachment.command.DeleteAttachmentCommand;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:25
 */
public interface DeleteAttachmentUseCase {
    void deleteAttachment(DeleteAttachmentCommand command);
}
