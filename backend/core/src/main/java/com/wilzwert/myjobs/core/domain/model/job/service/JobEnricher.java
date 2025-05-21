package com.wilzwert.myjobs.core.domain.model.job.service;

import com.wilzwert.myjobs.core.domain.model.job.EnrichedJob;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.shared.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class JobEnricher {

    public DomainPage<EnrichedJob> enrich(DomainPage<Job> domainPage, User user) {
        return DomainPage.builder(domainPage, enrich(domainPage.getContent(), user)).build();
    }

    public List<EnrichedJob> enrich(List<Job> jobs, User user) {
        Instant threshold = Instant.now().minus(user.getJobFollowUpReminderDays(), ChronoUnit.DAYS);
        return jobs.stream()
                .map(job -> enrich(job, threshold))
                .toList();
    }

    public EnrichedJob enrich(Job job, User user) {
        Instant threshold = Instant.now().minus(user.getJobFollowUpReminderDays(), ChronoUnit.DAYS);
        return enrich(job, threshold);
    }

    private EnrichedJob enrich(Job job, Instant threshold) {
        boolean isFollowUpLate = JobStatus.activeStatuses().contains(job.getStatus())
                && job.getStatusUpdatedAt().isBefore(threshold);
        return new EnrichedJob(job, isFollowUpLate);
    }
}