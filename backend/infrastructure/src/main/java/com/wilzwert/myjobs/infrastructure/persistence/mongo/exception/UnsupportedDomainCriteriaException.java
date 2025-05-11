package com.wilzwert.myjobs.infrastructure.persistence.mongo.exception;

public class UnsupportedDomainCriteriaException extends RuntimeException {
    public UnsupportedDomainCriteriaException(String message) {
        super(message);
    }
}
