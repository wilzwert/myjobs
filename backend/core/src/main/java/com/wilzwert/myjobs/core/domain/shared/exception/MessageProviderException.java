package com.wilzwert.myjobs.core.domain.shared.exception;


import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 * Date:19/05/2025
 * Time:10:35
 */

public class MessageProviderException extends DomainException {
    public MessageProviderException() {
        super(ErrorCode.CANNOT_SEND_MESSAGE);
    }
}
