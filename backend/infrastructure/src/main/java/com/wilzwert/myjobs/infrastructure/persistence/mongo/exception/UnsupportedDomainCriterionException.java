package com.wilzwert.myjobs.infrastructure.persistence.mongo.exception;

public class UnsupportedDomainCriterionException extends RuntimeException {
    public UnsupportedDomainCriterionException(String message) {
        super(message);
    }
}
