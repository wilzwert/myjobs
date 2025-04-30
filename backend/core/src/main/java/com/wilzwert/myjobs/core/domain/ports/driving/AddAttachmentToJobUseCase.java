package com.wilzwert.myjobs.core.domain.ports.driving;


import com.wilzwert.myjobs.core.domain.command.CreateAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:25
 */
public interface AddAttachmentToJobUseCase {
    Attachment addAttachmentToJob(CreateAttachmentCommand command);
}
