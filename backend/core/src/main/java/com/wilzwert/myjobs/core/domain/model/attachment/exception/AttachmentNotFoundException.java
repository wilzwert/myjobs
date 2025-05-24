package com.wilzwert.myjobs.core.domain.model.attachment.exception;


import com.wilzwert.myjobs.core.domain.shared.exception.EntityNotFoundException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 */

public class AttachmentNotFoundException extends EntityNotFoundException {
    public AttachmentNotFoundException() {
        super(ErrorCode.ATTACHMENT_NOT_FOUND);
    }
}
