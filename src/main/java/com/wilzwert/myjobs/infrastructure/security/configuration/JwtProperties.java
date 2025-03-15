package com.wilzwert.myjobs.infrastructure.security.configuration;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Provides server related configuration properties
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:16:05
 *
 */
@ConfigurationProperties(prefix = "security.jwt")
@Getter
@Setter
public class JwtProperties {
    private String secretKey;

    private long expirationTime;

    private long refreshExpirationTime;
}