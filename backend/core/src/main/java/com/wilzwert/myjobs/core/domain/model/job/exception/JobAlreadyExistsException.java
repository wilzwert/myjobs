package com.wilzwert.myjobs.core.domain.model.job.exception;


import com.wilzwert.myjobs.core.domain.shared.exception.EntityAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 */

public class JobAlreadyExistsException extends EntityAlreadyExistsException {
    public JobAlreadyExistsException() {
        super(ErrorCode.JOB_ALREADY_EXISTS);
    }
}
