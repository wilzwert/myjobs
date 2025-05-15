package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.JobReminderMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendJobsRemindersUseCase;
import com.wilzwert.myjobs.core.domain.shared.batch.UsersJobsBatchResult;
import com.wilzwert.myjobs.core.domain.shared.bulk.BulkServiceSaveResult;
import com.wilzwert.myjobs.core.domain.shared.collector.UsersJobsBatchCollector;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:16:55
 */

public class SendJobsRemindersUseCaseImpl implements SendJobsRemindersUseCase {

    private final JobService jobService;

    private final UserService userService;

    private final JobReminderMessageProvider jobReminderMessageProvider;


    public SendJobsRemindersUseCaseImpl(JobService jobService, UserService userService, JobReminderMessageProvider jobReminderMessageProvider) {
        this.jobService = jobService;
        this.userService = userService;
        this.jobReminderMessageProvider = jobReminderMessageProvider;
    }

    private UsersJobsBatchResult doSend(Map<User, Set<Job>> usersToJobs) {
        System.out.println("should send to "+usersToJobs.size()+" users");
        usersToJobs.forEach((u, j) -> System.out.println("Should send "+j.size()+" jobs to user "+u.getId()));

        Set<User> usersToSave = new HashSet<>();
        for(Map.Entry<User, Set<Job>> entry : usersToJobs.entrySet()) {
            jobReminderMessageProvider.send(entry.getKey(), entry.getValue());
            usersToSave.add(entry.getKey());
        }
        usersToSave.forEach(User::saveJobFollowUpReminderSentAt);
        BulkServiceSaveResult serviceResult = userService.saveAll(usersToSave);

        return new UsersJobsBatchResult(usersToJobs.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size())), serviceResult);
    }

    @Override
    public List<UsersJobsBatchResult> sendJobsReminders(int batchSize) {
        // load jobs to remind...important : as UsersJobsBatchCollector expects Jobs to be pre-sorted by userId
        // the sort is configured in the DomainSpecification.JobFollowUpToRemind spec
        Stream<Job> jobsToRemind = jobService.stream(DomainSpecification.JobFollowUpToRemind(Instant.now()));

        return jobsToRemind.collect(
                new UsersJobsBatchCollector(
                        userIds -> userService.findMinimal(DomainSpecification.In("id", userIds)),
                        this::doSend,
                        batchSize
                )
        );
    }
}
