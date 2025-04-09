package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;


import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Wilhelm Zwertvaegher
 * Date:09/04/2025
 * Time:13:45
 */

public class MongoRefreshTokenTest {

    @Test
    public void tokenShouldEqual() {
        MongoRefreshToken token = new MongoRefreshToken().setToken("myToken");

        assertEquals("myToken", token.getToken());
    }

    @Test
    public void expiresAtShouldEqual() {
        Instant now =  Instant.now();
        MongoRefreshToken token = new MongoRefreshToken().setToken("myToken");
        token.setExpiresAt(now);

        assertEquals(now, token.getExpiresAt());
    }
}