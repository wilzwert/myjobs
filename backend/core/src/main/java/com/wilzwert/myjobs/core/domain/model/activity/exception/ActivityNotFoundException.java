package com.wilzwert.myjobs.core.domain.model.activity.exception;


import com.wilzwert.myjobs.core.domain.shared.exception.EntityNotFoundException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 */

public class ActivityNotFoundException extends EntityNotFoundException {
    public ActivityNotFoundException() {
        super(ErrorCode.ACTIVITY_NOT_FOUND);
    }
}
