package com.wilzwert.myjobs.core.domain.model.job.ports.driving;


import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.attachment.command.CreateAttachmentsCommand;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface AddAttachmentToJobUseCase {
    List<Attachment> addAttachmentsToJob(CreateAttachmentsCommand command);
}
