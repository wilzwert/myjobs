package com.wilzwert.myjobs.core.domain.model;


import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/04/2025
 * Time:16:45
 */

public class UserTest {

    @Test
    public void shouldCreateUser() {
        UserId userId = new UserId(UUID.randomUUID());
        User user = User.create("test@example.com", "password", "username", "firstName", "lastName");
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("username", user.getUsername());
        assertEquals("firstName", user.getFirstName());
        assertEquals("lastName", user.getLastName());
    }
}
