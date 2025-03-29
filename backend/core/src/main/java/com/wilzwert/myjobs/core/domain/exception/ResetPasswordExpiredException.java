package com.wilzwert.myjobs.core.domain.exception;


/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:16
 */

public class ResetPasswordExpiredException extends RuntimeException {
    public ResetPasswordExpiredException() {
        super("Password reset token has expired.");
    }
}
