package com.wilzwert.myjobs.core.domain.model.user.exception;

import com.wilzwert.myjobs.core.domain.shared.exception.DomainException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

public class LoginException extends DomainException {
    public LoginException() {super(ErrorCode.USER_LOGIN_FAILED);}
}
