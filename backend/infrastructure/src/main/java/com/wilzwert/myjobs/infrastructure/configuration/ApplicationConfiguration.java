package com.wilzwert.myjobs.infrastructure.configuration;


import com.wilzwert.myjobs.infrastructure.event.kafka.KafkaProperties;
import com.wilzwert.myjobs.infrastructure.mail.MailProperties;
import com.wilzwert.myjobs.infrastructure.security.configuration.CookieProperties;
import com.wilzwert.myjobs.infrastructure.security.configuration.JwtProperties;
import com.wilzwert.myjobs.infrastructure.security.ratelimit.RateLimitingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Wilhelm Zwertvaegher
 */
@Configuration
@EnableConfigurationProperties({ CookieProperties.class, JwtProperties.class, RateLimitingProperties.class, MailProperties.class, KafkaProperties.class })
public class ApplicationConfiguration {
}
