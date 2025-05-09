package com.wilzwert.myjobs.infrastructure.security.ratelimit;

import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RateLimitingService {

    private final RateLimitingProperties rateLimitingProperties;

    public RateLimitingService(RateLimitingProperties rateLimitingProperties) {
        this.rateLimitingProperties = rateLimitingProperties;
    }

    @Cacheable(value = "rateLimitingRules", key = "#requestPath + ':' + #scope")
    public Optional<RateLimitingProperties.RateLimitConfig> findBestMatchingRule(String requestPath, String scope) {
        return rateLimitingProperties.getRules().stream()
                // match path start
                .filter(rule -> requestPath.startsWith(rule.getPath()))
                // filter by scope
                .filter(rule -> rule.getScope() == null || rule.getScope().equals(scope))
                // sort found rules by best path match then scope and take first (i.e. min)
                .min((a, b) -> {
                    int cmpPath = Integer.compare(b.getPath().length(), a.getPath().length());
                    if (cmpPath != 0) return cmpPath;
                    return Boolean.compare(a.getScope() == null, b.getScope() == null);
                });
    }

    @Cacheable(value = "rateLimitingBucket", key = "#key")
    public Bucket getBucket(String key, RateLimitingProperties.RateLimitConfig rateLimitConfig) {
        Bandwidth limit = Bandwidth.builder().capacity(rateLimitConfig.getLimit()).refillIntervally(rateLimitConfig.getLimit(), rateLimitConfig.getDuration()).build();
        return Bucket.builder().addLimit(limit).build();
    }

    // builds key to retrieve or create butcket, based on current request authentication status and rate limit config
    protected String buildKey(HttpServletRequest request, RateLimitingProperties.RateLimitConfig config) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String scope = (authentication == null || !authentication.isAuthenticated()) ? "anonymous" : "authenticated";
        String key = scope + ":" + config.getPath() + ":" + request.getRemoteAddr();
        if ("anonymous".equals(scope)) {
            // for an anonymous user the key will be something like anonymous:/api:127.0.0.1
            // TODO : improve the key to handle shared IP addresses, or users behind proxies

        } else {
            // for a logged-in user we use their username + ip
            // for now we assume that a user has only one active logged-in session per IP
            // this may be improved also
            // also, casting the Principal to UserDetailsImpl seems like unnecessary coupling, although the infra has the right
            // to now which UserDetails implementation may be used
            key +=  ":" + ((UserDetailsImpl) authentication.getPrincipal()).getId().value().toString();
        }
        return key;
    }
}
