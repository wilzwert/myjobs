package com.wilzwert.myjobs.infrastructure.security.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 *
 */

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;

    public RateLimitingFilter(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String scope = (authentication == null || !authentication.isAuthenticated()) ? "anonymous" : "authenticated";
        Optional<RateLimitingProperties.RateLimitConfig> configOpt = rateLimitingService.findBestMatchingRule(path, scope);

        if (configOpt.isPresent()) {
            RateLimitingProperties.RateLimitConfig config = configOpt.get();
            String key = rateLimitingService.buildKey(request, config);
            // retrieve or build bucket for that key
            Bucket bucket = rateLimitingService.getBucket(key, config);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            // add headers to  inform of rate limit status
            response.setHeader("X-Rate-Limit-Limit", String.valueOf(bucket.getAvailableTokens()));
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            response.setHeader("X-Rate-Limit-Reset", String.valueOf(probe.getNanosToWaitForRefill()/ 1_000_000_000));

            if(!probe.isConsumed()) {
                // too many requests
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many requests");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}