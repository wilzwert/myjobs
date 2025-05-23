package com.wilzwert.myjobs.infrastructure.security.internal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/23/2025
 * Filter used to authorize HTTP requests to the internal controller(s) (/internal/**)
 * Calls to these endpoints are used when local scheduler / batch cannot be used
 * These endpoints are for "private" use only
 * For now a simple validation based on a secret sent as a request header
 * that must match the application.internal.secret property
 */
@Component
@Slf4j
public class InternalEndpointAuthorization extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-Internal-Secret";

    private final String internalSecret;

    public InternalEndpointAuthorization(@Value("${application.internal.secret}") String internalSecret) {
        this.internalSecret = internalSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(HEADER_NAME);

        if (!internalSecret.equals(apiKey)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Unauthorized access");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // bypass filter if path should not be handled by this filter
        String path = request.getRequestURI();
        return !path.matches("/internal/.*");
    }
}
