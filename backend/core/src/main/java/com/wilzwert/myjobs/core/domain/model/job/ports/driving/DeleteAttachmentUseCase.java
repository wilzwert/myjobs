package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.attachment.command.DeleteAttachmentCommand;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface DeleteAttachmentUseCase {
    void deleteAttachment(DeleteAttachmentCommand command);
}
