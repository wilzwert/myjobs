package com.wilzwert.myjobs.core.domain.exception;


import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:17
 */

public class JobNotFoundException extends EntityNotFoundException {
    public JobNotFoundException() {
        super(ErrorCode.JOB_NOT_FOUND);
    }
}
