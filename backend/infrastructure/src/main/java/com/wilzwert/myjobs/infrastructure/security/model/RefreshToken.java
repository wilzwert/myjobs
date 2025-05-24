package com.wilzwert.myjobs.infrastructure.security.model;


import java.time.Instant;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface RefreshToken {
    String getToken();
    Instant getExpiresAt();
    void setExpiresAt(Instant expiresAt);
    UUID getUserId();

}
