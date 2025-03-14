package com.wilzwert.myapps.infrastructure.persistence.mongo.entity;


import com.wilzwert.myapps.infrastructure.security.model.RefreshToken;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:10:37
 */

@Data
@Accessors(chain = true)
@Document(collection = "refresh_token")
public class MongoRefreshToken implements RefreshToken {

    @Id
    private UUID id;

    @Field(name = "user_id")
    private UUID userId;

    @Field(name = "token")
    @Indexed(unique = true)
    private String token;

    @Field(name = "expiry_date")
    private Instant expiryDate;

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public Instant getExpiresAt() {
        return expiryDate;
    }
}
