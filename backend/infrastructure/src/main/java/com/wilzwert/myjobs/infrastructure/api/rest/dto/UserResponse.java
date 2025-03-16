package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import lombok.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:39
 */

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String email;

    private String username;

    private String role;
}
