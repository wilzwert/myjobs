package com.wilzwert.myjobs.core.domain.model.job.exception;


import com.wilzwert.myjobs.core.domain.shared.exception.EntityNotFoundException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 */

public class JobNotFoundException extends EntityNotFoundException {
    public JobNotFoundException() {
        super(ErrorCode.JOB_NOT_FOUND);
    }
}
