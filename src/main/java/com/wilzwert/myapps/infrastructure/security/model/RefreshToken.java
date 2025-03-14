package com.wilzwert.myapps.infrastructure.security.model;


import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:11:24
 */
public interface RefreshToken {
    String getToken();
    Instant getExpiresAt();

}
