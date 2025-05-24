package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;


import com.wilzwert.myjobs.core.domain.model.user.UserId;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Wilhelm Zwertvaegher
 */

public class IdMapperTest {

    private final IdMapper underTest = new IdMapper() {};

    @Test
    void shouldConvertEntityIdToUUID() {
        UUID uuid = UUID.randomUUID();
        UserId userId = new UserId(uuid);

        assertEquals(uuid, underTest.toEntity(userId));
    }

    @Test
    void shouldConvertUUIDToUserId() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, underTest.mapUserId(uuid).value());
    }

    @Test
    void shouldConvertUUIDToJobId() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, underTest.mapJobId(uuid).value());
    }

    @Test
    void shouldConvertUUIDToActivityId() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, underTest.mapActivityId(uuid).value());
    }

    @Test
    void shouldConvertUUIDToAttachmentId() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, underTest.mapAttachmentId(uuid).value());
    }
}
