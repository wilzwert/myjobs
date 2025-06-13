package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.job.JobState;
import com.wilzwert.myjobs.core.domain.model.user.UserSummary;
import com.wilzwert.myjobs.core.domain.model.user.UserView;
import com.wilzwert.myjobs.core.domain.model.user.collector.UserSummaryCollector;
import com.wilzwert.myjobs.core.domain.model.user.command.UpdateUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.command.UpdateUserLangCommand;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserUpdatedEvent;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.EmailVerificationMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.*;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.event.IntegrationEventPublisher;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.transaction.TransactionProvider;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */

public class UserUseCaseImpl implements SendVerificationEmailUseCase, GetUserViewUseCase, UpdateUserUseCase, UpdateUserLangUseCase, GetUserSummaryUseCase {

    private final TransactionProvider transactionProvider;

    private final IntegrationEventPublisher integrationEventPublisher;

    private final UserDataManager userDataManager;

    private final EmailVerificationMessageProvider emailVerificationMessageProvider;

    public UserUseCaseImpl(
            TransactionProvider transactionProvider,
            IntegrationEventPublisher integrationEventPublisher,
            UserDataManager userDataManager,
            EmailVerificationMessageProvider emailVerificationMessageProvider) {
        this.transactionProvider = transactionProvider;
        this.integrationEventPublisher = integrationEventPublisher;
        this.userDataManager = userDataManager;
        this.emailVerificationMessageProvider = emailVerificationMessageProvider;
    }

    @Override
    public void sendVerificationEmail(UserId userId) {
        userDataManager.findMinimalById(userId).ifPresent(emailVerificationMessageProvider::send);
    }

    @Override
    public User updateUser(UpdateUserCommand command) {
        User user = userDataManager.findMinimalById(command.userId()).orElseThrow(UserNotFoundException::new);

        // if email changes, check availability
        if(!command.email().equals(user.getEmail())) {
            User existingUser = userDataManager.findMinimalByEmail(command.email()).orElse(null);
            if(existingUser != null && !existingUser.equals(user)) {
                throw new UserAlreadyExistsException();
            }
        }
        // if username changes, check availability
        if(!command.username().equals(user.getUsername())) {
            User existingUser = userDataManager.findMinimalByUsername(command.username()).orElse(null);
            if(existingUser != null && !existingUser.equals(user)) {
                throw new UserAlreadyExistsException();
            }
        }

        boolean shouldResendVerificationEmail = !user.getEmail().equals(command.email());

        return transactionProvider.executeInTransaction(() -> {
            User updatedUser = user.update(command.email(), command.username(), command.firstName(), command.lastName(), command.jobFollowUpReminderDays());
            updatedUser = userDataManager.save(updatedUser);

            if (shouldResendVerificationEmail) {
                emailVerificationMessageProvider.send(updatedUser);
            }

            integrationEventPublisher.publish(new UserUpdatedEvent(IntegrationEventId.generate(), updatedUser.getId()));
            return updatedUser;
        });
    }

    @Override
    public User updateUserLang(UpdateUserLangCommand command) {
        User user = userDataManager.findMinimalById(command.userId()).orElseThrow(UserNotFoundException::new);
        if(user.getLang().equals(command.lang())) {
            return user;
        }

        return transactionProvider.executeInTransaction(() -> {
            User updatedUser = userDataManager.save(user.updateLang(command.lang()));
            integrationEventPublisher.publish(new UserUpdatedEvent(IntegrationEventId.generate(), updatedUser.getId()));
            return updatedUser;
        });
    }

    @Override
    public UserView getUser(UserId userId) {
        return userDataManager.findViewById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public UserSummary getUserSummary(UserId userId) {
        User user = userDataManager.findMinimalById(userId).orElseThrow(UserNotFoundException::new);

        List<JobState> jobStatuses = userDataManager.getJobsState(user);

        return jobStatuses.stream().collect(new UserSummaryCollector(user));
    }
}
