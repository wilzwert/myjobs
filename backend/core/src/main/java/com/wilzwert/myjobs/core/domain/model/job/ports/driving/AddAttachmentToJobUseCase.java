package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.attachment.command.CreateAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface AddAttachmentToJobUseCase {
    Attachment addAttachmentToJob(CreateAttachmentCommand command);
}
