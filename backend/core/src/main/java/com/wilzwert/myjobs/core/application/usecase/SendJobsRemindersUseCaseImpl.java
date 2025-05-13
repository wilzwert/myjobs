package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.model.user.UserView;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.JobReminderMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendJobsRemindersUseCase;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;

import java.util.List;

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

    @Override
    public void sendJobsReminders() {
        // load jobs


        // load a list of user views (ie not editable)
        List<UserView> users = userService.findView(
            DomainSpecification.And(List.of(
                DomainSpecification.Or(
                    List.of(
                        DomainSpecification.Eq("jobFollowUpReminderSentAt", null)
                    )
                )
            ))
        );


        // iterate through users
        //  load late jobs
        //  send late jobs reminder
        //  bulk update jobs followUpReminderSentAt
        //  update User.jobFollowUpReminderSentAt
    }
}
