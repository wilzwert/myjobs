package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.user.command.UpdateUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.EmailVerificationMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 */
@ExtendWith(MockitoExtension.class)
class UserUseCaseImplTest {

    @Mock
    private UserDataManager userDataManager;

    @Mock
    private EmailVerificationMessageProvider emailVerificationMessageProvider;

    @InjectMocks
    private UserUseCaseImpl underTest;

    private User getValidTestUser(UserId userId) {
        return User.builder()
                .id(userId)
                .email("text@example.com")
                .username("username")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .jobs(Collections.emptyList())
                .build();
    }

    @Test
    void whenUserExists_thenShouldSendVerificationEmail() {
        UserId userId = UserId.generate();
        User user = getValidTestUser(userId);
        when(userDataManager.findMinimalById(userId)).thenReturn(Optional.of(user));
        doNothing().when(emailVerificationMessageProvider).send(user);

        underTest.sendVerificationEmail(userId);

        verify(userDataManager, times(1)).findMinimalById(userId);
        verify(emailVerificationMessageProvider, times(1)).send(user);
    }

    @Test
    void whenEmailDoesntChange_thenShouldUpdateUserAndNotSendVerificationEmail() {
        UserId userId = UserId.generate();
        User user = getValidTestUser(userId);

        when(userDataManager.findMinimalById(userId)).thenReturn(Optional.of(user));
        when(userDataManager.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updatedUser = underTest.updateUser(new UpdateUserCommand(user.getEmail(), "updatedusername", "updatedfirstName", "updatedlastName", 12, userId));
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals(user.getEmail(), updatedUser.getEmail());
        assertEquals("updatedusername", updatedUser.getUsername());
        assertEquals("updatedfirstName", updatedUser.getFirstName());
        assertEquals("updatedlastName", updatedUser.getLastName());
        assertEquals(12, updatedUser.getJobFollowUpReminderDays());
        verify(userDataManager, times(1)).save(user);
        verify(emailVerificationMessageProvider, times(0)).send(user);
    }

    @Test
    void whenEmailChanges_thenShouldUpdateUserAndSendVerificationEmail() {
        UserId userId = UserId.generate();
        User user = getValidTestUser(userId);

        when(userDataManager.findMinimalById(userId)).thenReturn(Optional.of(user));
        when(userDataManager.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(emailVerificationMessageProvider).send(user);

        User updatedUser = underTest.updateUser(new UpdateUserCommand("other@example.com", "username", "firstName", "lastName", 12, userId));
        assertEquals(userId, updatedUser.getId());
        assertEquals("other@example.com", updatedUser.getEmail());
        assertEquals(EmailStatus.PENDING, updatedUser.getEmailStatus());
        assertEquals("username", updatedUser.getUsername());
        assertEquals("firstName", updatedUser.getFirstName());
        assertEquals("lastName", updatedUser.getLastName());
        assertEquals(12, updatedUser.getJobFollowUpReminderDays());
        verify(userDataManager, times(1)).save(user);
        verify(emailVerificationMessageProvider, times(1)).send(user);
    }
}
