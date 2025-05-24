package com.wilzwert.myjobs.infrastructure.security.jwt;


import com.wilzwert.myjobs.core.domain.model.user.AuthenticatedUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Wilhelm Zwertvaegher
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticatedUser implements AuthenticatedUser {

    private String email;

    private String username;

    private String jwtToken;

    private String refreshToken;

    private String role;
}
