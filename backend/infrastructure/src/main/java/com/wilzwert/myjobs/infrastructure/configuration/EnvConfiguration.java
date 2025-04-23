package com.wilzwert.myjobs.infrastructure.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/04/2025
 * Time:13:41
 */
@Configuration
@Profile({"dev","test","integration"})
// @PropertySource(value = "classpath:.env", ignoreResourceNotFound = true)
@PropertySource(value = "file:.env", ignoreResourceNotFound = true) // .env is used only in dev local environment
public class EnvConfiguration {
}
