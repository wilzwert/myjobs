package com.wilzwert.myjobs.infrastructure.security.configuration;


import com.wilzwert.myjobs.core.domain.model.user.ports.driven.Authenticator;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.infrastructure.adapter.DefaultPasswordHasher;
import com.wilzwert.myjobs.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.wilzwert.myjobs.infrastructure.security.jwt.JwtAuthenticator;
import com.wilzwert.myjobs.infrastructure.security.ratelimit.RateLimitingFilter;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import com.wilzwert.myjobs.infrastructure.security.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */
@Configuration
public class SecurityConfiguration {

    private final String frontendUrl;

    private final boolean corsAllowAll;

    public SecurityConfiguration(@Value("${application.frontend.url}") String frontendUrl, @Value("${security.cors.allow-all}") boolean corsAllowAll) {
        this.frontendUrl = frontendUrl;
        this.corsAllowAll = corsAllowAll;

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        if (corsAllowAll) {
            config.addAllowedOriginPattern("*");
        }
        else {
            config.setAllowedOrigins(List.of(frontendUrl));
        }
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordHasher passwordHasher(PasswordEncoder passwordEncoder) {
        return new DefaultPasswordHasher(passwordEncoder);
    }

    @Bean
    public Authenticator authenticator(JwtService jwtService, RefreshTokenService refreshTokenService) {
        return new JwtAuthenticator(jwtService, refreshTokenService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter, RateLimitingFilter rateLimitingFilter) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // disable CSRF protection, as the app is RESTful API
                .csrf(AbstractHttpConfigurer::disable)
                // RESTFul API should not use HTTP sessions
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        // allow publicly accessible paths
                        auth.requestMatchers(
                                        "/api/auth/register",
                                        "/api/auth/login",
                                        "/api/auth/logout",
                                        "/api/auth/refresh-token",
                                        "/api/auth/email-check",
                                        "/api/auth/username-check",
                                        "/api/user/password/**",
                                        "/api/user/me/email/validation",
                                        /*"/"+storageProperties.getUploadDir()+"/**",
                                        apiDocProperties.getApiDocsPath()+"/**",
                                        apiDocProperties.getSwaggerPath()+"/**",*/
                                        // note : we have to add /swagger-ui/** because event if swagger path is set in configuration
                                        // the ui is redirected to /swagger-ui/index.html
                                        "/swagger-ui/**",
                                        // used for "internal" http calls e.g. manual/scheduled http batch triggering
                                        "/internal/**"
                        ).permitAll()
                        // everything else requires authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exp -> exp.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                // insert our custom filter, which will authenticate user from token if provided in the request
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitingFilter, JwtAuthenticationFilter.class)
                .build();
    }
}
