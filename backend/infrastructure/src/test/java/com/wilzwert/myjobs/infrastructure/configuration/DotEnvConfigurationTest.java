package com.wilzwert.myjobs.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.core.io.FileSystemResourceLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DotEnvConfigurationTest {

    @TempDir
    Path tempDir;

    @Test
    void loadsLocalEnvFile() throws IOException {
        // Given: a .env file with a key=value
        Path envFile = tempDir.resolve(".env");
        try (BufferedWriter writer = Files.newBufferedWriter(envFile)) {
            writer.write("MY_KEY=my_value");
        }

        DotEnvConfiguration config = new DotEnvConfiguration() {
            @Override
            protected File getEnvFile() {
                return envFile.toFile();
            }
        };

        ConfigurableEnvironment environment = new MockEnvironment();
        config.postProcessEnvironment(environment, dummySpringApplication());

        // Then: environment should contain the loaded key
        assertEquals("my_value", environment.getProperty("MY_KEY"));
    }

    @Test
    void usesOverridePathIfPresent() throws IOException {
        // Given: a .env file that points to another file with DOT_ENV_PATH
        Path overrideFile = tempDir.resolve("override.env");
        try (BufferedWriter writer = Files.newBufferedWriter(overrideFile)) {
            writer.write("OTHER_KEY=override_value");
        }

        Path mainEnv = tempDir.resolve(".env");
        try (BufferedWriter writer = Files.newBufferedWriter(mainEnv)) {
            writer.write("OTHER_KEY=override_value");
        }

        DotEnvConfiguration config = new DotEnvConfiguration() {
            @Override
            protected File getEnvFile() {
                return mainEnv.toFile();
            }
        };

        ConfigurableEnvironment environment = new MockEnvironment();
        config.postProcessEnvironment(environment, dummySpringApplication());

        assertEquals("override_value", environment.getProperty("OTHER_KEY"));
    }

    @Test
    void doesNothingIfNoEnvFilePresent() {
        DotEnvConfiguration config = new DotEnvConfiguration() {
            @Override
            protected File getEnvFile() {
                return new File(tempDir.resolve(".env").toString()); // non-existent
            }
        };

        ConfigurableEnvironment environment = new MockEnvironment();
        config.postProcessEnvironment(environment, dummySpringApplication());

        // No exception should be thrown and no properties should be added
        assertNull(environment.getProperty("ANYTHING"));
    }

    // Dummy SpringApplication to satisfy method signature
    private SpringApplication dummySpringApplication() {
        return new SpringApplication(Object.class) {
            @Override public org.springframework.core.io.ResourceLoader getResourceLoader() {
                return new FileSystemResourceLoader(); // Required for ResourcePropertySource
            }
        };
    }
}