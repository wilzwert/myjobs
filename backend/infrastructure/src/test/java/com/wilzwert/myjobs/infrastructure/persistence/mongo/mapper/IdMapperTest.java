package com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper;


import com.wilzwert.myjobs.core.domain.model.UserId;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Wilhelm Zwertvaegher
 * Date:10/04/2025
 * Time:08:38
 */

public class IdMapperTest {

    private final IdMapper underTest = new IdMapper() {};

    @Test
    public void shouldConvertEntityIdToUUID() {
        UUID uuid = UUID.randomUUID();
        UserId userId = new UserId(uuid);

        assertEquals(uuid, underTest.toEntity(userId));
    }

    @Test
    public void shouldConvertUUIDToUserId() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, underTest.mapUserId(uuid).value());
    }

    @Test
    public void shouldConvertUUIDToJobId() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, underTest.mapJobId(uuid).value());
    }

    @Test
    public void shouldConvertUUIDToActivityId() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, underTest.mapActivityId(uuid).value());
    }

    @Test
    public void shouldConvertUUIDToAttachmentId() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, underTest.mapAttachmentId(uuid).value());
    }
}
