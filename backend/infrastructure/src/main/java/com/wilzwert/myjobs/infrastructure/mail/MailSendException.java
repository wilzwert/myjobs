package com.wilzwert.myjobs.infrastructure.mail;

public class MailSendException extends RuntimeException {
    public MailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
