package com.wilzwert.myjobs.infrastructure.exception;

public class BatchRunException extends RuntimeException {
    public BatchRunException(String message, Throwable cause) {
        super(message, cause);
    }
}
