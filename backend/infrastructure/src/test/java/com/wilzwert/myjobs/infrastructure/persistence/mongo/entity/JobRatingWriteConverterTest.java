package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;

import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 */



public class JobRatingWriteConverterTest {

    private final JobRatingWriteConverter converter = new JobRatingWriteConverter();

    @Test
    void shouldConvertJobRatingToInteger() {
        JobRating rating = JobRating.of(3);
        Integer value = converter.convert(rating);
        assertEquals(3, value);
    }

    @Test
    void shouldThrowExceptionWhenSourceIsNull() {
        assertThrows(NullPointerException.class, () -> converter.convert(null));
    }
}
