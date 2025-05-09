package com.wilzwert.myjobs.infrastructure.security.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:04/11/2024
 * Time:11:08
 */

@ExtendWith(MockitoExtension.class)
@Tag("Security")
public class RateLimitingFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private RateLimitingService rateLimitingService;

    @InjectMocks
    private RateLimitingFilter underTest;

    @AfterEach
    void afterEach() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void whenRateLimitExceeded_thenShouldRejectRequestAndSetHeaders() throws Exception {
        String path = "/api/test";
        String scope = "anonymous";
        String key = "anonymous:/api/test:127.0.0.1";

        RateLimitingProperties.RateLimitConfig config = new RateLimitingProperties.RateLimitConfig("/api", scope, 5, Duration.ofSeconds(10));

        Bucket mockBucket = mock(Bucket.class);
        ConsumptionProbe probe = ConsumptionProbe.rejected(5, 2_000_000_000, 2_000_000_000); // 2s to refill

        when(request.getRequestURI()).thenReturn(path);
        when(rateLimitingService.findBestMatchingRule(path, scope)).thenReturn(Optional.of(config));
        when(rateLimitingService.buildKey(request, config)).thenReturn(key);
        when(rateLimitingService.getBucket(key, config)).thenReturn(mockBucket);
        when(mockBucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);

        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        underTest.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        assertTrue(writer.toString().contains("Too many requests"));

        verify(response).setHeader("X-Rate-Limit-Limit", "0");
        verify(response).setHeader("X-Rate-Limit-Remaining", "5");
        verify(response).setHeader("X-Rate-Limit-Reset", "2");
    }


    @Test
    void whenTokenAvailable_thenShouldAllowRequestAndSetHeaders() throws Exception {
        // given
        String path = "/api/jobs";
        String scope = "anonymous";
        String key = "anonymous:/api/jobs:127.0.0.1";
        Bucket bucket = mock(Bucket.class);
        ConsumptionProbe probe = mock(ConsumptionProbe.class);

        RateLimitingProperties.RateLimitConfig config = new RateLimitingProperties.RateLimitConfig("/api", scope, 10, Duration.ofSeconds(60));

        when(request.getRequestURI()).thenReturn(path);
        when(rateLimitingService.findBestMatchingRule(path, scope)).thenReturn(Optional.of(config));
        when(rateLimitingService.buildKey(request, config)).thenReturn(key);
        when(rateLimitingService.getBucket(key, config)).thenReturn(bucket);

        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(true);
        when(probe.getRemainingTokens()).thenReturn(9L);
        when(probe.getNanosToWaitForRefill()).thenReturn(5_000_000_000L); // 5s
        when(bucket.getAvailableTokens()).thenReturn(10L);

        // when
        underTest.doFilterInternal(request, response, filterChain);

        // then
        verify(response).setHeader("X-Rate-Limit-Limit", "10");
        verify(response).setHeader("X-Rate-Limit-Remaining", "9");
        verify(response).setHeader("X-Rate-Limit-Reset", "5");
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    }
}