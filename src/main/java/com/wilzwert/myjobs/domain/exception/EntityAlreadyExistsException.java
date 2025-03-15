package com.wilzwert.myjobs.domain.exception;

public abstract class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String message) {super(message);}
}
