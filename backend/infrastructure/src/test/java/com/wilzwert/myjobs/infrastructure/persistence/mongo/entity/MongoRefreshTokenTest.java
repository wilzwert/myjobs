package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;


import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Wilhelm Zwertvaegher
 */

public class MongoRefreshTokenTest {

    @Test
    void tokenShouldEqual() {
        MongoRefreshToken token = new MongoRefreshToken().setToken("myToken");

        assertEquals("myToken", token.getToken());
    }

    @Test
    void expiresAtShouldEqual() {
        Instant now =  Instant.now();
        MongoRefreshToken token = new MongoRefreshToken().setToken("myToken");
        token.setExpiresAt(now);

        assertEquals(now, token.getExpiresAt());
    }
}