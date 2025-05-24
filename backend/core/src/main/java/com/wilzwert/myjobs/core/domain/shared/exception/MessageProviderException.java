package com.wilzwert.myjobs.core.domain.shared.exception;


import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 */

public class MessageProviderException extends DomainException {
    public MessageProviderException() {
        super(ErrorCode.CANNOT_SEND_MESSAGE);
    }
}
