package com.wilzwert.myjobs.domain.ports.driven;


/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:14
 */
public interface PasswordHasher {
    String hashPassword(String password);
    boolean verifyPassword(String password, String hashedPassword);
}
