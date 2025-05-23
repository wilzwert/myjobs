package com.wilzwert.myjobs.infrastructure.security.ratelimit;


import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:04/11/2024
 * Time:11:08
 */

@ExtendWith(MockitoExtension.class)
@Tag("Security")
public class RateLimitingServiceTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private RateLimitingProperties rateLimitingProperties;

    @InjectMocks
    private RateLimitingService underTest;

    private static final List<RateLimitingProperties.RateLimitConfig> rules = List.of(
        new RateLimitingProperties.RateLimitConfig("/api", null, 10, Duration.ofSeconds(60)),
        new RateLimitingProperties.RateLimitConfig("/api", "authenticated", 50, Duration.ofSeconds(30)),
        new RateLimitingProperties.RateLimitConfig("/api/jobs", "authenticated", 30, Duration.ofSeconds(30)),
        new RateLimitingProperties.RateLimitConfig("/api/auth/email-check", "anonymous", 10, Duration.ofSeconds(60)),
        new RateLimitingProperties.RateLimitConfig("/api/auth/email-check", "authenticated", 30, Duration.ofSeconds(30))
    );

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @CsvSource({
        "'/api/user', 'anonymous', '/api', , 10, 60",
        "'/api/something', 'authenticated', '/api', 'authenticated', 50, 30",
        "'/api/jobs', 'anonymous', '/api', , 10, 60",
        "'/api/jobs', 'authenticated', '/api/jobs', 'authenticated' , 30, 30",
        "'/api/auth/email-check', 'authenticated', '/api/auth/email-check', 'authenticated' , 30, 30",
        "'/api/auth/email-check', 'anonymous', '/api/auth/email-check', 'anonymous' , 10, 60",
        "'/api/auth/email-check', '', '/api',  , 10, 60"
    })
    void shouldReturnMostSpecificRateLimitRuleBasedOnPathAndScope(String path, String scope, String expectedPath, String expectedScope, int expectedLimit, long expectedDuration ) {
        when(rateLimitingProperties.getRules()).thenReturn(rules);
        Optional<RateLimitingProperties.RateLimitConfig> found = underTest.findBestMatchingRule(path, scope);
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getPath()).isEqualTo(expectedPath);
        assertThat(found.get().getScope()).isEqualTo(expectedScope);
        assertThat(found.get().getLimit()).isEqualTo(expectedLimit);
        assertThat(found.get().getDuration().toSeconds()).isEqualTo(expectedDuration);
    }

    @Test
    void whenNoMatchingRuleFound_thenShouldReturnEmpty() {
        when(rateLimitingProperties.getRules()).thenReturn(rules);
        assertThat(underTest.findBestMatchingRule("/something", "authenticated").isPresent()).isFalse();
        assertThat(underTest.findBestMatchingRule("/something", "anonymous").isPresent()).isFalse();
    }

    @Test
    void whenNoRulesExist_thenShouldReturnEmpty() {
        when(rateLimitingProperties.getRules()).thenReturn(Collections.emptyList());
        assertThat(underTest.findBestMatchingRule("/something", "authenticated").isPresent()).isFalse();
        assertThat(underTest.findBestMatchingRule("/something", "anonymous").isPresent()).isFalse();
    }

    @Test
    void shouldBuildAnonymousKey() {
        var config = new RateLimitingProperties.RateLimitConfig("/api", "anonymous", 50, Duration.ofSeconds(30));
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        assertThat(underTest.buildKey(request, config)).isEqualTo("anonymous:/api:127.0.0.1");
        verify(request).getRemoteAddr();
    }

    @Test
    void shouldBuildAuthenticatedKey() {
        UUID id = UUID.randomUUID();
        var config = new RateLimitingProperties.RateLimitConfig("/api", "authenticated", 50, Duration.ofSeconds(30));
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(new UserDetailsImpl(
                new UserId(id),
                "test@example.com",
                "username",
                "USER",
                "password",
                Collections.emptyList()
            ),
            null,
                    List.of(new SimpleGrantedAuthority("USER"))
        ));

        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        assertThat(underTest.buildKey(request, config)).isEqualTo("authenticated:/api:127.0.0.1:"+id);

        verify(request, times(1)).getRemoteAddr();
    }

    @Test
    void shouldBuildBucket() {
        var config = new RateLimitingProperties.RateLimitConfig("/api", "authenticated", 50, Duration.ofSeconds(30));
        var key = "test-bucket";

        Bucket bucket = underTest.getBucket(key, config);
        assertThat(bucket).isNotNull();
        assertThat(bucket.getAvailableTokens()).isEqualTo(50);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        assertThat(probe).isNotNull();
        assertThat(probe.getRemainingTokens()).isEqualTo(49);
        // this is a very permissive test as there should be no way 2 seconds have passed
        assertThat(probe.getNanosToWaitForReset() / 1_000_000).isGreaterThan(28);
    }
}