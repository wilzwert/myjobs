package com.wilzwert.myjobs.infrastructure.security.model;


import java.time.Instant;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:11:24
 */
public interface RefreshToken {
    String getToken();
    Instant getExpiresAt();
    void setExpiresAt(Instant expiresAt);
    UUID getUserId();

}
