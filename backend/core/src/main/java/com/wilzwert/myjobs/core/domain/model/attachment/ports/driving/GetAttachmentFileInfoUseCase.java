package com.wilzwert.myjobs.core.domain.model.attachment.ports.driving;


import com.wilzwert.myjobs.core.domain.model.AttachmentFileInfo;
import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import com.wilzwert.myjobs.core.domain.model.attachment.command.DownloadAttachmentCommand;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface GetAttachmentFileInfoUseCase {

    AttachmentFileInfo getAttachmentFileInfo(DownloadAttachmentCommand command);
}
