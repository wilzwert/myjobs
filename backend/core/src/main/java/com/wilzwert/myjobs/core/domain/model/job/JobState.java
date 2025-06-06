package com.wilzwert.myjobs.core.domain.model.job;


import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Represent a Job's state : status / updatedAt, statusUpdatedAt
 * Used to compute user's summary
 * {@link com.wilzwert.myjobs.core.domain.model.user.UserSummary}
 */

public record JobState(JobStatus status, Instant updatedAt, Instant statusUpdatedAt) {}