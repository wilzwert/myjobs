package com.wilzwert.myjobs.core.domain.shared.exception;

import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

public class PaginationException extends DomainException {
    public PaginationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
