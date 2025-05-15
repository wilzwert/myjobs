package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.JobReminderMessageProvider;
import com.wilzwert.myjobs.infrastructure.mail.MailProvider;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JobReminderMessageProviderAdapter implements JobReminderMessageProvider {

    private final MailProvider mailProvider;

    public JobReminderMessageProviderAdapter(final MailProvider mailProvider) {
        this.mailProvider = mailProvider;
    }

    @Override
    public void send(User user, Set<Job> jobs) {
        try {
            var message = mailProvider.createMessage("mail/jobs_reminder", user.getEmail(), user.getFirstName(), "email.jobs_reminder.subject", user.getLang().toString());
            message.setVariable("url", mailProvider.createMeUrl(message.getLocale()));
            message.setVariable("user", user);
            message.setVariable("jobs", jobs);
            message.setVariable("jobsUrls", jobs.stream().collect(Collectors.toMap(Job::getId, (job -> mailProvider.createUrl("uri.job", message.getLocale(), job.getId().value().toString())))));
            message.setVariable("firstName", user.getFirstName());
            message.setVariable("lastName", user.getLastName());
            message.setVariable("validationUrl", mailProvider.createUrl("uri.email_validation", message.getLocale(), user.getEmailValidationCode()));
            mailProvider.send(message);
        }
        // TODO : improve exception handling
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
