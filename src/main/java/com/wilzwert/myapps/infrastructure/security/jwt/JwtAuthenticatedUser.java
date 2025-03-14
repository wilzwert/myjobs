package com.wilzwert.myapps.infrastructure.security.jwt;


import com.wilzwert.myapps.domain.model.AuthenticatedUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:19:46
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
