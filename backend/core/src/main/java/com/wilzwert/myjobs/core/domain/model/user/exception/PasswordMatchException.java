package com.wilzwert.myjobs.core.domain.model.user.exception;

import com.wilzwert.myjobs.core.domain.shared.exception.DomainException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

public class PasswordMatchException extends DomainException {
    public PasswordMatchException() {super(ErrorCode.USER_PASSWORD_MATCH_FAILED);}
}
