package com.wilzwert.myjobs.core.domain.model.user.exception;


import com.wilzwert.myjobs.core.domain.shared.exception.EntityAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 */

public class UserAlreadyExistsException extends EntityAlreadyExistsException {
    public UserAlreadyExistsException() {super(ErrorCode.USER_ALREADY_EXISTS);}
}
