package com.wilzwert.myjobs.core.domain.shared.exception;


import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 */

public class MalformedUrlException extends DomainException {
    public MalformedUrlException() {
        super(ErrorCode.INVALID_URL);
    }
}
