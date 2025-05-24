package com.wilzwert.myjobs.core.domain.model.attachment.exception;


import com.wilzwert.myjobs.core.domain.shared.exception.DomainException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 */

public class AttachmentFileNotReadableException extends DomainException {
    public AttachmentFileNotReadableException() {
        super(ErrorCode.ATTACHMENT_FILE_NOT_READABLE);
    }
}
