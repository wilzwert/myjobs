package com.wilzwert.myjobs.infrastructure.configuration;


import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * @author Wilhelm Zwertvaegher
 * Date:20/03/2025
 * Time:08:56
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public Caffeine caffeineConfig() {
        return Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES);
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("emailExists", "usernameExists");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES) // expires after 1 hour
                .maximumSize(1000) // Max 1000 entrées
        );
        return cacheManager;
    }
}
