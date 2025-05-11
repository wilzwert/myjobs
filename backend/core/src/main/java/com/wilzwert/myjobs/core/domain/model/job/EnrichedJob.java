package com.wilzwert.myjobs.core.domain.model.job;

import com.wilzwert.myjobs.core.domain.model.DomainEnrichedEntity;

public record EnrichedJob(Job job, boolean isFollowUpLate) implements DomainEnrichedEntity {
}
