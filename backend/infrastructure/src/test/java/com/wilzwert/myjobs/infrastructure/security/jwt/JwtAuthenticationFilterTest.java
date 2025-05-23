package com.wilzwert.myjobs.infrastructure.security.jwt;


import com.wilzwert.myjobs.infrastructure.security.model.JwtToken;
import com.wilzwert.myjobs.infrastructure.security.service.CookieService;
import com.wilzwert.myjobs.infrastructure.security.service.CustomUserDetailsService;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:04/11/2024
 * Time:11:08
 */

@ExtendWith(MockitoExtension.class)
@Tag("Security")
public class JwtAuthenticationFilterTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private CookieService cookieService;


    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    /*
    @Mock
    private ApiDocProperties apiDocProperties;*/

    @InjectMocks
    private JwtAuthenticationFilter underTest;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

    }
    @Test
    void shouldReturnTrueWhenFilteringNotNecessaryOnEmptyRequest() {
        assertThrows(NullPointerException.class, () -> underTest.shouldNotFilter(null));
    }


    /*
    @Test
    void shouldReturnTrueWhenFilteringNotNecessaryForApi() {
        when(apiDocProperties.getApiDocsPath()).thenReturn("/api/doc");
        when(request.getRequestURI()).thenReturn("/api/doc");

        assertThat(underTest.shouldNotFilter(request)).isTrue();
    }*/

    @Test
    void shouldReturnTrueWhenFilteringNotNecessaryForAuth() {
        when(request.getRequestURI()).thenReturn("/api/auth/register");

        assertThat(underTest.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldReturnTrueWhenFilteringNotNecessaryForInternal() {
        when(request.getRequestURI()).thenReturn("/internal/jobs-reminders-batch");

        assertThat(underTest.shouldNotFilter(request)).isTrue();
    }


    /*
    @Test
    void shouldReturnTrueWhenFilteringNotNecessaryForSwagger() {
        when(apiDocProperties.getApiDocsPath()).thenReturn("/api/doc");
        when(apiDocProperties.getSwaggerPath()).thenReturn("/api/swagger");
        when(request.getRequestURI()).thenReturn("/api/swagger");

        assertThat(underTest.shouldNotFilter(request)).isTrue();
    }*/

    @Test
    void shouldReturnTrueWhenFilteringNotNecessaryForSwaggerUi() {
        when(request.getRequestURI()).thenReturn("/swagger-ui/");

        assertThat(underTest.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenFilteringNecessary() {
        when(request.getRequestURI()).thenReturn("/api/jobs/");

        assertThat(underTest.shouldNotFilter(request)).isFalse();
    }

    @Test
    void shouldAuthenticateUser() throws Exception {
        when(customUserDetailsService.loadUserByUsername("1")).thenReturn(new User("1", "password", Collections.emptyList()));

        Claims claims = Jwts.claims().subject("1").build();
        JwtToken jwtToken = new JwtToken("1", claims);
        when(jwtService.extractTokenFromRequest(request)).thenReturn(Optional.of(jwtToken));

        underTest.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    void shouldNotAuthenticateUserWhenAuthenticationNotNull() throws Exception {
        Claims claims = Jwts.claims().subject("1").build();
        JwtToken jwtToken = new JwtToken("1", claims);
        when(jwtService.extractTokenFromRequest(request)).thenReturn(Optional.of(jwtToken));

        SecurityContextHolder.getContext().setAuthentication(new Authentication() {
             @Override
             public Collection<? extends GrantedAuthority> getAuthorities() {
                 return List.of();
             }

             @Override
             public Object getCredentials() {
                 return null;
             }

             @Override
             public Object getDetails() {
                 return null;
             }

             @Override
             public Object getPrincipal() {
                 return null;
             }

             @Override
             public boolean isAuthenticated() {
                 return false;
             }

             @Override
             public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

             }

             @Override
             public String getName() {
                 return "";
             }
         });

        underTest.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    void shouldNotAuthenticateUserWhenUserNotFoundInClaims() throws Exception {
        Claims claims = Jwts.claims().subject(null).build();
        JwtToken jwtToken = new JwtToken("", claims);
        when(jwtService.extractTokenFromRequest(request)).thenReturn(Optional.of(jwtToken));

        underTest.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldNotAuthenticateUserWhenTokenExtractionThrowsException() throws Exception {
        when(jwtService.extractTokenFromRequest(request)).thenThrow(JwtException.class);
        doNothing().when(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        doNothing().when(writer).print("auth_error");
        when(cookieService.revokeAccessTokenCookie()).thenReturn(ResponseCookie.from("access_token", "").build());
        when(cookieService.revokeRefreshTokenCookie()).thenReturn(ResponseCookie.from("refresh_token", "").build());
        doNothing().when(response).addHeader(anyString(), anyString());

        underTest.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(cookieService, times(1)).revokeAccessTokenCookie();
        verify(cookieService, times(1)).revokeRefreshTokenCookie();
        verify(response, times(2)).addHeader(anyString(), anyString());
    }

    @Test
    void shouldNotAuthenticateUserWhenTokenInvalid() throws Exception {
        when(jwtService.extractTokenFromRequest(request)).thenReturn(Optional.empty());

        underTest.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}