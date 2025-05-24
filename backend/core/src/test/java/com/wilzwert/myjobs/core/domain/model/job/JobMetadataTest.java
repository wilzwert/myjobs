package com.wilzwert.myjobs.core.domain.model.job;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Wilhelm Zwertvaegher
 */

class JobMetadataTest {

    @Test
    void testJobMetadataEquals() {
        JobMetadata expected = new JobMetadata.Builder()
                .title("Title")
                .build();

        JobMetadata tested = new JobMetadata.Builder()
                .title("Title")
                .build();

        assertEquals(expected, tested);
    }

    @Test
    void testJobMetadataNotEquals() {
        JobMetadata expected = new JobMetadata.Builder()
                .title("Title")
                .build();

        JobMetadata tested = new JobMetadata.Builder()
                .title("Other Title")
                .build();

        assertNotEquals(expected, tested);
    }
}
