package com.wilzwert.myjobs.core.domain.model.user.collector;


import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.user.UserSummary;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author Wilhelm Zwertvaegher
 * Date:03/06/2025
 * Time:09:51
 */

public class UserSummaryCollector implements Collector<JobStatus, Map<JobStatus, Integer>, UserSummary> {

    private int jobsCount = 0;

    @Override
    public Supplier<Map<JobStatus, Integer>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<JobStatus, Integer>, JobStatus> accumulator() {
        return (statusesToCount, jobStatus) -> {
            jobsCount++;
            statusesToCount.compute(jobStatus, (jobStatus1, integer) -> integer == null ? 1 : integer + 1);
        };
    }

    @Override
    public BinaryOperator<Map<JobStatus, Integer>> combiner() {
        return (set1, set2) -> { throw new UnsupportedOperationException("Parallel processing is not supported"); };
    }

    @Override
    public Function<Map<JobStatus, Integer>, UserSummary> finisher() {
        return (statusToCount) -> {
            int activeJobsCount = statusToCount.entrySet().stream()
                   .filter(e -> JobStatus.activeStatuses().contains(e.getKey()))
                   .mapToInt(Map.Entry::getValue).sum();
            int inactiveJobsCount = jobsCount - activeJobsCount;
            return new UserSummary(jobsCount, activeJobsCount, inactiveJobsCount, statusToCount);
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }
}
