package com.wilzwert.myjobs.core.domain.shared.exception;


import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:16
 */

public class MalformedUrlException extends DomainException {
    public MalformedUrlException() {
        super(ErrorCode.INVALID_URL);
    }
}
