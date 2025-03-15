package com.wilzwert.myjobs.domain.exception;

public class LoginException extends RuntimeException {
    public LoginException() {
        super("Login failed");
    }
}
