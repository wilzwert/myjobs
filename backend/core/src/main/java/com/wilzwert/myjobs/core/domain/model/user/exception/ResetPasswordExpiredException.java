package com.wilzwert.myjobs.core.domain.model.user.exception;


import com.wilzwert.myjobs.core.domain.shared.exception.DomainException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 */

public class ResetPasswordExpiredException extends DomainException {
    public ResetPasswordExpiredException() {
        super(ErrorCode.USER_PASSWORD_RESET_EXPIRED);
    }
}
