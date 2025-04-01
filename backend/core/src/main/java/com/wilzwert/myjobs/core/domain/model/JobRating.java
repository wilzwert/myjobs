package com.wilzwert.myjobs.core.domain.model;


import java.util.Objects;

/**
 * @author Wilhelm Zwertvaegher
 * Date:01/04/2025
 * Time:13:27
 */

public class JobRating {
    private final int value;

    private JobRating(int value) {
        if (value < 0 || value > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5, half-points are allowed.");
        }
        this.value = value;
    }

    public static JobRating of(int value) {
        return new JobRating(value);
    }

    public int getValue() {
        return value;
    }

    public boolean isValid() {
        return value >= 0 && value <= 5;
    }

    @Override
    public String toString() {
        return String.format("Rating{value=%d}", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobRating rating = (JobRating) o;
        return Double.compare(rating.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}