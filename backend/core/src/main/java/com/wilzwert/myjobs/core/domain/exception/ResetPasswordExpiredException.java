package com.wilzwert.myjobs.core.domain.exception;


import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:16
 */

public class ResetPasswordExpiredException extends DomainException {
    public ResetPasswordExpiredException() {
        super(ErrorCode.USER_PASSWORD_RESET_EXPIRED);
    }
}
