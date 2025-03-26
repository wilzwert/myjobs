package com.wilzwert.myjobs.core.domain.exception;


/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:17
 */

public class AttachmentFileNotReadableException extends EntityNotFoundException {
    public AttachmentFileNotReadableException() {
        super("File could not be read");
    }
}
