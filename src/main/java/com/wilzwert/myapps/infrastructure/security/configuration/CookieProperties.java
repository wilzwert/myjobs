package com.wilzwert.myapps.infrastructure.security.configuration;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Provides server related configuration properties
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:05
 *
 */
@ConfigurationProperties(prefix = "security.cookie")
@Getter
@Setter
public class CookieProperties {
    private boolean secure;

    private String sameSite;

    private String domain;

    private String path;
}