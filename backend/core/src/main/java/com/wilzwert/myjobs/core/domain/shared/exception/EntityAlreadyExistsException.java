package com.wilzwert.myjobs.core.domain.shared.exception;

import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

public abstract class EntityAlreadyExistsException extends DomainException {
    public EntityAlreadyExistsException(ErrorCode errorCode) {super(errorCode);}
}
