package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobDataManager;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.batch.UsersJobsRemindersBulkResult;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.JobReminderMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendJobsRemindersUseCase;
import com.wilzwert.myjobs.core.domain.shared.bulk.BulkDataSaveResult;
import com.wilzwert.myjobs.core.domain.shared.collector.UsersJobsBatchCollector;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Wilhelm Zwertvaegher
 */

public class SendJobsRemindersUseCaseImpl implements SendJobsRemindersUseCase {

    private final JobDataManager jobDataManager;

    private final UserDataManager userDataManager;

    private final JobReminderMessageProvider jobReminderMessageProvider;


    public SendJobsRemindersUseCaseImpl(JobDataManager jobDataManager, UserDataManager userDataManager, JobReminderMessageProvider jobReminderMessageProvider) {
        this.jobDataManager = jobDataManager;
        this.userDataManager = userDataManager;
        this.jobReminderMessageProvider = jobReminderMessageProvider;
    }

    /**
     * This is the main logic for sending jobs reminders to a users/jobs chunk :
     * - send a message by user through the JobReminderMessageProvider
     * - save the jobs with their new followUpReminderSentAt through the JobDataManager
     * - save the users with their new jobFollowUpReminderSentAt through the UserDataManager
     * A reference to this method is passed to the batch collector
     * This method could (should ?) be moved to a domain service, but for now it will do
     * @param usersToJobs map of user -> jobs, the relevant users and jobs
     * @return the results for this chunk
     */
    private UsersJobsRemindersBulkResult doSend(Map<User, Set<Job>> usersToJobs) {
        List<String> errors = new ArrayList<>();
        Set<User> usersToSave = new HashSet<>();
        int totalJobs = 0;
        for(Map.Entry<User, Set<Job>> entry : usersToJobs.entrySet()) {

            try {
                jobReminderMessageProvider.send(entry.getKey(), entry.getValue());
                usersToSave.add(entry.getKey());
                // we have to map the jobs Set because the saveFollowUpReminderSentAt method returns a copy of the Job
                Set<Job> jobsToSave = entry.getValue().stream().map(Job::saveFollowUpReminderSentAt).collect(Collectors.toSet());
                jobDataManager.saveAll(jobsToSave);
                totalJobs += jobsToSave.size();
            }
            catch (Exception e) {
                errors.add("An error occurred while sending reminders to "+entry.getKey()+": "+e.getMessage());
            }
        }
        // we have to map the users set because saveJobFollowUpReminderSentAt returns a copy of the User
        usersToSave = usersToSave.stream().map(User::saveJobFollowUpReminderSentAt).collect(Collectors.toSet());
        BulkDataSaveResult serviceResult = null;
        if(!usersToSave.isEmpty()) {
             serviceResult = userDataManager.saveAll(usersToSave);
        }
        int saveErrors = serviceResult != null ? serviceResult.totalCount()-serviceResult.updatedCount() : usersToSave.size();
        return new UsersJobsRemindersBulkResult(usersToJobs.size(), totalJobs, errors, errors.size(), saveErrors);
    }

    /**
     *  The entrypoint to this use case
     *  This method loads all the jobs that need reminders in a Stream, hen uses a custom collector which will iterate through
     *  the stream, chunk it and load the users through the provided callback (userDataManager.findMinimal...)
     *  and finally use a reference to this::doSend to actually send the reminders and collect and return the results
     * @param batchSize the chunk size passed by the infra ; as the infra knows what size can be handled
     * @return a list of results the infra may or may not use
     */
    @Override
    public List<UsersJobsRemindersBulkResult> sendJobsReminders(int batchSize) {
        // load jobs to remind...important : as UsersJobsBatchCollector expects Jobs to be pre-sorted by userId
        // the sort is configured in the DomainSpecification.JobFollowUpToRemind spec
        Stream<Job> jobsToRemind = jobDataManager.stream(DomainSpecification.JobFollowUpToRemind(Instant.now()));
        return jobsToRemind.collect(
                new UsersJobsBatchCollector<>(
                        userIds -> userDataManager.findMinimal(DomainSpecification.in("id", userIds)),
                        this::doSend,
                        batchSize
                )
        );
    }
}
