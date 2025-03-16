package com.wilzwert.myjobs.core.domain.exception;


/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:16
 */

public class WeakPasswordException extends RuntimeException {
    public WeakPasswordException() {
        super("Password does not meet security requirements.");
    }
}
