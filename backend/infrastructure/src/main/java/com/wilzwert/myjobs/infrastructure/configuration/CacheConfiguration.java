package com.wilzwert.myjobs.infrastructure.configuration;


import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * @author Wilhelm Zwertvaegher
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        CaffeineCache emailCache = new CaffeineCache("emailExists",
                Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES) // expires after 1 hour
                .maximumSize(1000)
                .build()
        );

        CaffeineCache usernameCache = new CaffeineCache("usernameExists",
                Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES) // expires after 1 hour
                        .maximumSize(1000)
                        .build()
        );

        CaffeineCache urlMetadataCache = new CaffeineCache("urlMetadataExists",
                Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES) // expires after 10 minutes
                        .maximumSize(1000)
                        .build()
        );

        CaffeineCache rulesCache = new CaffeineCache("rateLimitingRules",
                Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES) // expires after 10 minutes
                        .maximumSize(1000)
                        .build()
        );

        CaffeineCache bucketCache = new CaffeineCache("rateLimitingBucket",
                Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES) // expires after 10 minutes
                        .maximumSize(1000)
                        .build()
        );

        cacheManager.setCaches(Arrays.asList(emailCache, usernameCache, urlMetadataCache, rulesCache, bucketCache));
        return cacheManager;
    }
}
