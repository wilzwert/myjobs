package com.wilzwert.myjobs.core.domain.exception;

import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

public class LoginException extends DomainException {
    public LoginException() {super(ErrorCode.USER_LOGIN_FAILED);}
}
