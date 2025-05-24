package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.user.ports.driven.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Wilhelm Zwertvaegher
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
