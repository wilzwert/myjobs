package com.wilzwert.myjobs.infrastructure.batch;

public class BatchRunException extends RuntimeException {
    public BatchRunException(String message, Throwable cause) {
        super(message, cause);
    }
}
