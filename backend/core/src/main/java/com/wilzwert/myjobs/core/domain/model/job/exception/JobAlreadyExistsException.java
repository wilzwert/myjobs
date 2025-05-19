package com.wilzwert.myjobs.core.domain.model.job.exception;


import com.wilzwert.myjobs.core.domain.shared.exception.EntityAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:17
 */

public class JobAlreadyExistsException extends EntityAlreadyExistsException {
    public JobAlreadyExistsException() {
        super(ErrorCode.JOB_ALREADY_EXISTS);
    }
}
