package com.wilzwert.myjobs.infrastructure.security.ratelimit;

import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Duration;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RateLimitingIT extends AbstractBaseIntegrationTest  {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public RateLimitingProperties rateLimitingProperties() {
            RateLimitingProperties props = new RateLimitingProperties();

            RateLimitingProperties.RateLimitConfig rule = new RateLimitingProperties.RateLimitConfig("/api/user/me", "authenticated", 2, Duration.ofMinutes(1));
            props.setRules(List.of(rule));
            return props;
        }
    }

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    // id for the User to use for get /api/user tests
    private static final String USER_FOR_GET_TEST_ID = "abcd4321-4321-4321-4321-123456789012";

    @Test
    void shouldLimitRequestsBasedOnIpAndPath() throws Exception {
        Cookie accessTokenCookie = new Cookie("access_token", jwtService.generateToken(USER_FOR_GET_TEST_ID));
        // 1st request: allowed
        mockMvc.perform(get("/api/user/me").cookie(accessTokenCookie).with(remoteAddr("1.2.3.4")))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Rate-Limit-Remaining", "1"));

        // 2nd request: allowed
        mockMvc.perform(get("/api/user/me").cookie(accessTokenCookie).with(remoteAddr("1.2.3.4")))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Rate-Limit-Remaining", "0"));

        // 3rd request: blocked
        mockMvc.perform(get("/api/user/me").cookie(accessTokenCookie).with(remoteAddr("1.2.3.4")))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().string(containsString("Too many requests")));
    }

    @Test
    void whenNoRuleApplies_thenShouldLNotLimitRequests() throws Exception {
        Cookie accessTokenCookie = new Cookie("access_token", jwtService.generateToken(USER_FOR_GET_TEST_ID));
        // 1st request: allowed
        mockMvc.perform(get("/api/jobs").cookie(accessTokenCookie).with(remoteAddr("1.2.3.4")))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("X-Rate-Limit-Remaining"));

        // 2nd request: allowed
        mockMvc.perform(get("/api/jobs").cookie(accessTokenCookie).with(remoteAddr("1.2.3.4")))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("X-Rate-Limit-Remaining"));

        // 3rd request: blocked
        mockMvc.perform(get("/api/jobs").cookie(accessTokenCookie).with(remoteAddr("1.2.3.4")))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("X-Rate-Limit-Remaining"));
    }

    private RequestPostProcessor remoteAddr(String ip) {
        return request -> {
            request.setRemoteAddr(ip);
            return request;
        };
    }
}
