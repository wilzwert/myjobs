package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import lombok.Data;

@Data
public class PasswordRequest {
    private String token;

    private String oldPassword;

    private String password;
}
