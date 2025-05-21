package com.wilzwert.myjobs.core.domain.model.user.exception;

import com.wilzwert.myjobs.core.domain.shared.exception.DomainException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

public class UserDeleteException extends DomainException {
    public UserDeleteException() {
        super(ErrorCode.USER_DELETE_FAILED);
    }
}
