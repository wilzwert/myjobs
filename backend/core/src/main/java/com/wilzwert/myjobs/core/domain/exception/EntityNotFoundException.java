package com.wilzwert.myjobs.core.domain.exception;

import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

public abstract class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(ErrorCode code) {super(code);}
}
