package com.wilzwert.myjobs.infrastructure.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:17:18
 */

@Configuration
@PropertySource("file:.env")
public class AppConfiguration {
}
