package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.wilzwert.myjobs.core.domain.model.JobRating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Wilhelm Zwertvaegher
 * Date:10/04/2025
 * Time:09:57
 */

public class JobRatingConverterTest {

    private final JobRatingConverter underTest = new JobRatingConverter();

    @Test
    public void shouldThrowNumberFormatException_whenRatingIsNotAnInteger() {
        assertThrows(NumberFormatException.class, () -> underTest.convert("1.0"));
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenRatingIsLessThanZero() {
        assertThrows(IllegalArgumentException.class, () -> underTest.convert("-1"));
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenRatingIsGreaterThanFive() {
        assertThrows(IllegalArgumentException.class, () -> underTest.convert("+6"));
    }

    @Test
    public void shouldConvertRating() {
        JobRating expected = JobRating.of(1);
        assertEquals(expected, underTest.convert("1"));
    }
}
