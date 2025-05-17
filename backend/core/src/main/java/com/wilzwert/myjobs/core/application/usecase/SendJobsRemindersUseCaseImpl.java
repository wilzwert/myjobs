package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.batch.UsersJobsRemindersBatchResult;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.JobReminderMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendJobsRemindersUseCase;
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

    private UsersJobsRemindersBatchResult doSend(Map<User, Set<Job>> usersToJobs) {
        List<String> errors = new ArrayList<>();
        Set<User> usersToSave = new HashSet<>();
        for(Map.Entry<User, Set<Job>> entry : usersToJobs.entrySet()) {

            try {
                jobReminderMessageProvider.send(entry.getKey(), entry.getValue());
                usersToSave.add(entry.getKey());
                // we have to map the jobs set because saveFollowUpReminderSentAt returns a new job
                Set<Job> jobsToSave = entry.getValue().stream().map(Job::saveFollowUpReminderSentAt).collect(Collectors.toSet());
                jobService.saveAll(jobsToSave);
            }
            catch (Exception e) {
                errors.add("An error occurred while sending reminders to "+entry.getKey()+": "+e.getMessage());
            }
        }
        // we have to map the users set because saveJobFollowUpReminderSentAt returns a new user
        usersToSave = usersToSave.stream().map(User::saveJobFollowUpReminderSentAt).collect(Collectors.toSet());
        BulkServiceSaveResult serviceResult = null;
        if(!usersToSave.isEmpty()) {
             serviceResult = userService.saveAll(usersToSave);
        }
        int saveErrors = serviceResult != null ? serviceResult.totalCount()-serviceResult.updatedCount() : usersToSave.size();
        return new UsersJobsRemindersBatchResult(0, 0, errors, errors.size(), saveErrors);
    }

    @Override
    public List<UsersJobsRemindersBatchResult> sendJobsReminders(int batchSize) {
        // load jobs to remind...important : as UsersJobsBatchCollector expects Jobs to be pre-sorted by userId
        // the sort is configured in the DomainSpecification.JobFollowUpToRemind spec
        Stream<Job> jobsToRemind = jobService.stream(DomainSpecification.JobFollowUpToRemind(Instant.now()));
        return jobsToRemind.collect(
                new UsersJobsBatchCollector<>(
                        userIds -> userService.findMinimal(DomainSpecification.In("id", userIds)),
                        this::doSend,
                        batchSize
                )
        );
    }
}
