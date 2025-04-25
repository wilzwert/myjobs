package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.command.UpdateUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.EmailVerificationMessageProvider;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:09/04/2025
 * Time:09:16
 */
@ExtendWith(MockitoExtension.class)
public class UserUseCaseImplTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailVerificationMessageProvider emailVerificationMessageProvider;

    @InjectMocks
    private UserUseCaseImpl underTest;

    @Test
    public void shouldSendVerificationEmail_whenUserExists() {
        UserId userId = UserId.generate();
        User user = User.builder().id(userId).build();

        when(userService.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(emailVerificationMessageProvider).send(user);

        underTest.sendVerificationEmail(userId);

        verify(userService, times(1)).findById(userId);
        verify(emailVerificationMessageProvider, times(1)).send(user);
    }

    @Test
    public void shouldUpdateUserAndNotSendVerificationEmail_whenEmailDoesntChange() {
        UserId userId = UserId.generate();
        User user = User.builder().id(userId).email("test@example.com").build();

        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(userService.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updatedUser = underTest.updateUser(new UpdateUserCommand("test@example.com", "username", "firstName", "lastName", userId));
        assertEquals(userId, updatedUser.getId());
        assertEquals("test@example.com", updatedUser.getEmail());
        assertEquals("username", updatedUser.getUsername());
        assertEquals("firstName", updatedUser.getFirstName());
        assertEquals("lastName", updatedUser.getLastName());
        verify(userService, times(1)).save(user);
        verify(emailVerificationMessageProvider, times(0)).send(user);
    }

    @Test
    public void shouldUpdateUserAndSendVerificationEmail_whenEmailChanges() {
        UserId userId = UserId.generate();
        User user = User.builder().id(userId).email("test@example.com").build();

        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(userService.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(emailVerificationMessageProvider).send(user);

        User updatedUser = underTest.updateUser(new UpdateUserCommand("other@example.com", "username", "firstName", "lastName", userId));
        assertEquals(userId, updatedUser.getId());
        assertEquals("other@example.com", updatedUser.getEmail());
        assertEquals(EmailStatus.PENDING, updatedUser.getEmailStatus());
        assertEquals("username", updatedUser.getUsername());
        assertEquals("firstName", updatedUser.getFirstName());
        assertEquals("lastName", updatedUser.getLastName());
        verify(userService, times(1)).save(user);
        verify(emailVerificationMessageProvider, times(1)).send(user);
    }
}
