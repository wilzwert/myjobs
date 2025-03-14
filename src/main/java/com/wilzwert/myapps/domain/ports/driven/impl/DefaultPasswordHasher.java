package com.wilzwert.myapps.domain.ports.driven.impl;

import com.wilzwert.myapps.domain.ports.driven.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:14
 */
public class DefaultPasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public DefaultPasswordHasher(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean verifyPassword(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }
}
