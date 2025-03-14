package com.wilzwert.myapps.infrastructure.security.configuration;


import com.wilzwert.myapps.domain.ports.driven.Authenticator;
import com.wilzwert.myapps.domain.ports.driven.PasswordHasher;
import com.wilzwert.myapps.domain.ports.driven.impl.DefaultPasswordHasher;
import com.wilzwert.myapps.infrastructure.security.jwt.JwtAuthenticator;
import com.wilzwert.myapps.infrastructure.security.service.JwtService;
import com.wilzwert.myapps.infrastructure.security.service.RefreshTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:12:00
 */
@Configuration
public class SecurityConfiguration {

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // disable CSRF protection, as the app is RESTful API
                .csrf(AbstractHttpConfigurer::disable)
                // RESTFul API should not use HTTP sessions
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        // allow publicly accessible paths
                        auth.requestMatchers(
                                        "/api/auth/register",
                                        "/api/auth/login",
                                        /*"/"+storageProperties.getUploadDir()+"/**",
                                        apiDocProperties.getApiDocsPath()+"/**",
                                        apiDocProperties.getSwaggerPath()+"/**",*/
                                        // note : we have to add /swagger-ui/** because event if swagger path is set in configuration
                                        // the ui is redirected to /swagger-ui/index.html
                                        "/swagger-ui/**"
                        ).permitAll()
                        // everything else requires authentication
                        .anyRequest().authenticated()
                )
                // TODO ?.authenticationProvider(authenticationProvider())
                // insert our custom filter, which will authenticate user from token if provided in the request
                // TODO ? .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
