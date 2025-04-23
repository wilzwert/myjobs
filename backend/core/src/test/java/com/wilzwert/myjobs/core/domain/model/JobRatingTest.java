package com.wilzwert.myjobs.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:23/04/2025
 * Time:14:02
 */
public class JobRatingTest {

    @Test
    void shouldCreateValidJobRating() {
        JobRating rating = JobRating.of(3);
        assertEquals(3, rating.getValue());
        assertTrue(rating.isValid());
    }

    @Test
    void shouldThrowExceptionForNegativeValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> JobRating.of(-1));
        assertEquals("Rating must be between 0 and 5, decimals are not allowed.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForValueGreaterThan5() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> JobRating.of(6));
        assertEquals("Rating must be between 0 and 5, decimals are not allowed.", exception.getMessage());
    }

    @Test
    void shouldReturnCorrectToString() {
        JobRating rating = JobRating.of(4);
        assertEquals("Rating{value=4}", rating.toString());
    }

    @Test
    void shouldBeEqualIfValuesAreSame() {
        JobRating r1 = JobRating.of(2);
        JobRating r2 = JobRating.of(2);
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldNotBeEqualIfValuesAreDifferent() {
        JobRating r1 = JobRating.of(1);
        JobRating r2 = JobRating.of(4);
        assertNotEquals(r1, r2);
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentType() {
        JobRating rating = JobRating.of(2);
        assertNotEquals(rating, null);
        assertNotEquals(rating, "not a rating");
    }
}
