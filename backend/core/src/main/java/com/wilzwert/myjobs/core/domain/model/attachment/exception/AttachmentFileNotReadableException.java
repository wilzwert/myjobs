package com.wilzwert.myjobs.core.domain.model.attachment.exception;


import com.wilzwert.myjobs.core.domain.shared.exception.DomainException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:17
 */

public class AttachmentFileNotReadableException extends DomainException {
    public AttachmentFileNotReadableException() {
        super(ErrorCode.ATTACHMENT_FILE_NOT_READABLE);
    }
}
