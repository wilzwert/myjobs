package com.wilzwert.myjobs.core.domain.model.user.ports.driven;


/**
 * @author Wilhelm Zwertvaegher
 */
public interface PasswordHasher {
    String hashPassword(String password);
    boolean verifyPassword(String password, String hashedPassword);
}
