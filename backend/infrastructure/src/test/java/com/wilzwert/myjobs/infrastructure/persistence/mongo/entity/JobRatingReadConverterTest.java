package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;

import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:23/04/2025
 * Time:13:46
 */

public class JobRatingReadConverterTest {

    private final JobRatingReadConverter converter = new JobRatingReadConverter();

    @Test
    void shouldConvertIntegerToJobRating() {
        JobRating rating = converter.convert(4);
        assertNotNull(rating);
        assertEquals(4, rating.getValue());
    }

    @Test
    void shouldThrowExceptionForInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(6)); // supposition : 6 est invalide
        assertThrows(IllegalArgumentException.class, () -> converter.convert(-1));
        assertThrows(NullPointerException.class, () -> converter.convert(null));
    }
}

