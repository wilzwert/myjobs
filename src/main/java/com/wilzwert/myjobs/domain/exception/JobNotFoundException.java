package com.wilzwert.myjobs.domain.exception;


/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:17
 */

public class JobNotFoundException extends EntityNotFoundException {
    public JobNotFoundException() {
        super("Job not found");
    }
}
