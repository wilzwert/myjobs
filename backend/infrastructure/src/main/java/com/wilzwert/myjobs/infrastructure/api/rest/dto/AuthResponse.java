package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import lombok.*;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String email;

    private String username;

    private String role;
}
