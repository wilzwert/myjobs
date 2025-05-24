package com.wilzwert.myjobs.infrastructure.security.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.*;

/**
 * @author Wilhelm Zwertvaegher
 */

@ConfigurationProperties(prefix = "rate-limiting")
public class RateLimitingProperties {
    private List<RateLimitConfig> rules = new ArrayList<>();

    public List<RateLimitConfig> getRules() {
        return rules;
    }

    public void setRules(List<RateLimitConfig> rules) {
        this.rules = rules;
    }

    public static class RateLimitConfig {
        private String path;
        private String scope; // "anonymous", "authenticated", or null
        private int limit;
        private Duration duration;

        public RateLimitConfig(String path, String scope, int limit, Duration duration) {
            this.path = path;
            this.scope = scope;
            this.limit = limit;
            this.duration = duration;
        }

        // Getters and setters
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }
        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }
        public Duration getDuration() { return duration; }
        public void setDuration(Duration duration) { this.duration = duration; }
    }
}