package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:18:31
 */
@Data
public class LoginRequest {
    private String email;

    private String password;
}
