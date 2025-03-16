package com.wilzwert.myjobs.core.domain.exception;

public abstract class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {super(message);}
}
