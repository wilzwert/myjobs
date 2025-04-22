package com.wilzwert.myjobs.infrastructure.configuration;


import com.wilzwert.myjobs.infrastructure.security.configuration.CookieProperties;
import com.wilzwert.myjobs.infrastructure.security.configuration.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:16:05
 */
@Configuration
@EnableConfigurationProperties({ CookieProperties.class, JwtProperties.class})
public class ApplicationConfiguration {
}
