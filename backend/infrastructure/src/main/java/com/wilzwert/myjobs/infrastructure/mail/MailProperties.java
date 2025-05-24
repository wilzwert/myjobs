package com.wilzwert.myjobs.infrastructure.mail;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Provides server related configuration properties
 * @author Wilhelm Zwertvaegher
 *
 */
@ConfigurationProperties(prefix = "application.mail")
@Getter
@Setter
public class MailProperties {
    private String from;

    private String fromName;
}