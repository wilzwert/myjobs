package com.wilzwert.myjobs.core.domain.model.user.collector;


import com.wilzwert.myjobs.core.domain.model.job.JobState;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.job.JobStatusMeta;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserSummary;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Wilhelm Zwertvaegher
 * Date:03/06/2025
 * Time:09:51
 */

public class UserSummaryCollector implements Collector<JobState, Map<JobStatus, List<JobState>>, UserSummary> {


    private final User user;

    private int jobsCount = 0;

    public UserSummaryCollector(User user) {
        this.user = user;
    }

    @Override
    public Supplier<Map<JobStatus, List<JobState>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<JobStatus, List<JobState>>, JobState> accumulator() {
        return (statusesToCount, jobState) -> {
            jobsCount++;
            statusesToCount.computeIfAbsent(jobState.status(), jobStatus -> new ArrayList<>() ).add(jobState);
        };
    }

    @Override
    public BinaryOperator<Map<JobStatus, List<JobState>>> combiner() {
        return (set1, set2) -> { throw new UnsupportedOperationException("Parallel processing is not supported"); };
    }

    @Override
    public Function<Map<JobStatus, List<JobState>>, UserSummary> finisher() {
        return statusToStates -> {
            // FIXME : it may actually be better to loop through statusToStates
            // than to use numerous streams

            // map statuses to jobs count
            Map<JobStatus, Integer> statusToCount = statusToStates.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));

            // compute active, inactive jobs counts
            int activeJobsCount = statusToStates.entrySet().stream()
                   .filter(e -> JobStatus.activeStatuses().contains(e.getKey()))
                   .mapToInt(e -> e.getValue().size()).sum();
            int inactiveJobsCount = jobsCount - activeJobsCount;

            // compute usable filters
            Set<JobStatusMeta> filters = new HashSet<>();
            if(statusToStates.entrySet().stream().anyMatch(e -> JobStatus.activeStatuses().contains(e.getKey()))) {
                filters.add(JobStatusMeta.ACTIVE);
            }
            if(statusToStates.entrySet().stream().anyMatch(e -> JobStatus.inactiveStatuses().contains(e.getKey()))) {
                filters.add(JobStatusMeta.INACTIVE);
            }

            long lateJobsCount = statusToStates.entrySet().stream()
                    .filter(e -> JobStatus.activeStatuses().contains(e.getKey()))
                    .flatMap(jobStatusListEntry -> jobStatusListEntry.getValue().stream())
                    .filter(state -> user.isJobLate(state.statusUpdatedAt()))
                    .count();

            if(lateJobsCount > Integer.MAX_VALUE) {
                lateJobsCount = Integer.MAX_VALUE;
            }

            if(lateJobsCount > 0) {
                filters.add(JobStatusMeta.LATE);
            }

            return new UserSummary(jobsCount, activeJobsCount, inactiveJobsCount, (int)lateJobsCount, statusToCount, filters);
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }
}
