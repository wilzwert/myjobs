package com.wilzwert.myjobs.core.domain.exception;

public class LoginException extends RuntimeException {
    public LoginException() {
        super("Login failed");
    }
}
