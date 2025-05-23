package com.wilzwert.myjobs.infrastructure.mail;


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
@ConfigurationProperties(prefix = "application.mail")
@Getter
@Setter
public class MailProperties {
    private String from;

    private String fromName;
}