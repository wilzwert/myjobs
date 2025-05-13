package com.wilzwert.myjobs.core.domain.shared.exception;

import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

public class DomainException extends RuntimeException {
    private final ErrorCode errorCode;

    public DomainException(ErrorCode code) {
        super();
        errorCode = code;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
