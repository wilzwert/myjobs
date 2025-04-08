package com.wilzwert.myjobs.core.domain.model;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Wilhelm Zwertvaegher
 * Date:04/04/2025
 * Time:16:23
 */

public class JobMetadataTest {

    @Test
    public void testJobMetadataEquals() {
        JobMetadata expected = new JobMetadata.Builder()
                .title("Title")
                .build();

        JobMetadata tested = new JobMetadata.Builder()
                .title("Title")
                .build();

        assertEquals(expected, tested);
    }

    @Test
    public void testJobMetadataNotEquals() {
        JobMetadata expected = new JobMetadata.Builder()
                .title("Title")
                .build();

        JobMetadata tested = new JobMetadata.Builder()
                .title("Other Title")
                .build();

        assertNotEquals(expected, tested);
    }
}
