package com.wilzwert.myjobs.core.domain.shared.exception;

import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

public abstract class EntityNotFoundException extends DomainException {
    protected EntityNotFoundException(ErrorCode code) {super(code);}
}
