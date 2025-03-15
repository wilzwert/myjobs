package com.wilzwert.myjobs.domain.exception;


/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:17
 */

public class UserAlreadyExistsException extends EntityAlreadyExistsException {
    public UserAlreadyExistsException() {
        super("User already exists");
    }
}
