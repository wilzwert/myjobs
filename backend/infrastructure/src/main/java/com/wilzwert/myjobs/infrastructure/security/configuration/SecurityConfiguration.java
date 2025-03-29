package com.wilzwert.myjobs.infrastructure.security.configuration;


import com.wilzwert.myjobs.core.domain.ports.driven.Authenticator;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.infrastructure.adapter.DefaultPasswordHasher;
import com.wilzwert.myjobs.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.wilzwert.myjobs.infrastructure.security.jwt.JwtAuthenticator;
import com.wilzwert.myjobs.infrastructure.security.service.CustomUserDetailsService;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import com.wilzwert.myjobs.infrastructure.security.service.RefreshTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:12:00
 */
@Configuration
public class SecurityConfiguration {

    private final CustomUserDetailsService userDetailsService;

    private final JwtService jwtService;

    public SecurityConfiguration(CustomUserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
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
                                        "/api/auth/refresh-token",
                                        "/api/auth/email-check",
                                        "/api/auth/username-check",
                                        "/api/auth/password/**",
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
                .exceptionHandling(exp -> exp.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                // insert our custom filter, which will authenticate user from token if provided in the request
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService()), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Provide our custom UserDetailsService to the security component
     * @return UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    /**
     * Configure the app's AuthenticationProvide with our custom elements
     * @return AuthenticationProvider
     */
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
