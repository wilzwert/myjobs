package com.wilzwert.myjobs.core.domain.model.user.exception;


import com.wilzwert.myjobs.core.domain.shared.exception.EntityNotFoundException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 */

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException() {super(ErrorCode.USER_NOT_FOUND);}
}
