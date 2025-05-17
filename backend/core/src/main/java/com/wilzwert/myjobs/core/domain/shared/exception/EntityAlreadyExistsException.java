package com.wilzwert.myjobs.core.domain.shared.exception;

import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

public abstract class EntityAlreadyExistsException extends DomainException {
    protected EntityAlreadyExistsException(ErrorCode errorCode) {super(errorCode);}
}
