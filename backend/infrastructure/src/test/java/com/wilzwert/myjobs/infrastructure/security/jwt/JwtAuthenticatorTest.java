package com.wilzwert.myjobs.infrastructure.security.jwt;

import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoRefreshToken;
import com.wilzwert.myjobs.infrastructure.security.model.RefreshToken;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import com.wilzwert.myjobs.infrastructure.security.service.RefreshTokenService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:09/04/2025
 * Time:17:10
 */
@ExtendWith(MockitoExtension.class)
@Tag("Security")
public class JwtAuthenticatorTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private JwtAuthenticator underTest;

    @Test
    public void shoudAuthenticate() {
        UserId userId = UserId.generate();
        User user = User.builder().id(userId).email("test@example.com").username("test").password("password").firstName("firstName").lastName("lastName").role("USER").build();
        when(jwtService.generateToken(userId.value().toString())).thenReturn("token");
        RefreshToken refreshToken = new MongoRefreshToken().setToken("refresh_token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        JwtAuthenticatedUser authenticatedUser = (JwtAuthenticatedUser) underTest.authenticate(user);

        assertNotNull(authenticatedUser);
        assertEquals("test", authenticatedUser.getUsername());
        assertEquals("test@example.com", authenticatedUser.getEmail());
        assertEquals("refresh_token", authenticatedUser.getRefreshToken());
        assertEquals("USER", authenticatedUser.getRole());

        verify(jwtService, times(1)).generateToken(userId.value().toString());
        verify(refreshTokenService, times(1)).createRefreshToken(user);


    }


}
