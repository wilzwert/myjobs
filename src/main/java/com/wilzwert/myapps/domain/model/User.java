package com.wilzwert.myapps.domain.model;


import com.wilzwert.myapps.domain.command.RegisterUserCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:32
 */

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String email;
    private String password;
    private String username;
    private String firstName;
    private String lastName;
    private String role;
    private Instant createdAt;
    private Instant updatedAt;

    public static User fromCommand(RegisterUserCommand command) {
        return new User(
            UUID.randomUUID(),
            command.email(),
            command.password(),
            command.username(),
            command.firstName(),
            command.lastName(),
            "",
            null,
                null
        );
    }
}
