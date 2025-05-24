package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Wilhelm Zwertvaegher
 */

public class JobRatingConverterTest {

    private final JobRatingConverter underTest = new JobRatingConverter();

    @Test
    void shouldThrowNumberFormatException_whenRatingIsNotAnInteger() {
        assertThrows(NumberFormatException.class, () -> underTest.convert("1.0"));
    }

    @Test
    void shouldThrowIllegalArgumentException_whenRatingIsLessThanZero() {
        assertThrows(IllegalArgumentException.class, () -> underTest.convert("-1"));
    }

    @Test
    void shouldThrowIllegalArgumentException_whenRatingIsGreaterThanFive() {
        assertThrows(IllegalArgumentException.class, () -> underTest.convert("+6"));
    }

    @Test
    void shouldConvertRating() {
        JobRating expected = JobRating.of(1);
        assertEquals(expected, underTest.convert("1"));
    }
}
