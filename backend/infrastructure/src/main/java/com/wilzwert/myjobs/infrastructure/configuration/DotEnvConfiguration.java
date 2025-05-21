package com.wilzwert.myjobs.infrastructure.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/04/2025
 * Time:13:41
 * Loads .env before application context is refreshed
 * Mostly useful to configure log levels with a .env file in dev environment
 */
public class DotEnvConfiguration implements EnvironmentPostProcessor {
    private static final String ENV_FILE_NAME = ".env";
    private static final String DOT_ENV_PATH_KEY = "DOT_ENV_PATH";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            File baseEnv = getEnvFile();
            if (baseEnv.exists()) {
                handleLocalEnv(baseEnv, environment);
            }

        } catch (IOException e) {
            throw new UncheckedIOException("❌ Failed to load .env file.", e);
        }
    }

    protected File getEnvFile() {
        return new File(ENV_FILE_NAME);
    }

    private void handleLocalEnv(File envFile, ConfigurableEnvironment environment) throws IOException {
        Properties props = new Properties();
        try(FileInputStream fis = new FileInputStream(envFile)) {
            props.load(fis);
        }

        String overridePath = props.getProperty(DOT_ENV_PATH_KEY);

        if (overridePath != null && !overridePath.isBlank()) {
            System.out.println("Override path: " + overridePath);
            loadEnvFile(new File(overridePath), environment, "DOT_ENV_PATH");
        } else {
            loadEnvFile(envFile, environment, "local .env");
        }
    }

    private void loadEnvFile(File file, ConfigurableEnvironment environment, String origin) throws IOException {
        ResourcePropertySource source = new ResourcePropertySource(new FileSystemResource(file));
        environment.getPropertySources().addLast(source);
        System.out.println("✅ Loaded " + origin + ": " + file.getAbsolutePath());
    }
}