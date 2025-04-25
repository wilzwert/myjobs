package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.command.ChangePasswordCommand;
import com.wilzwert.myjobs.core.domain.command.CreatePasswordCommand;
import com.wilzwert.myjobs.core.domain.exception.PasswordMatchException;
import com.wilzwert.myjobs.core.domain.exception.ResetPasswordExpiredException;
import com.wilzwert.myjobs.core.domain.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordResetMessageProvider;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:09/04/2025
 * Time:10:31
 */
@ExtendWith(MockitoExtension.class)
public class PasswordUseCaseImplTest {
    @Mock
    private UserService userService;

    @Mock
    private PasswordResetMessageProvider passwordResetMessageProvider;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private PasswordUseCaseImpl underTest;

    @Test
    public void createNewPassword_shouldDoNothing_whenPasswordResetTokenNotFound() {
        CreatePasswordCommand createPasswordCommand = new CreatePasswordCommand("newPassword", "passwordResetToken");
        when(userService.findByResetPasswordToken("passwordResetToken")).thenReturn(Optional.empty());

        underTest.createNewPassword(createPasswordCommand);

        verify(userService, times(1)).findByResetPasswordToken("passwordResetToken");
        verify(passwordHasher, times(0)).hashPassword(anyString());
        verify(userService, times(0)).save(any(User.class));


    }

    @Test
    public void createNewPassword_shouldSaveUser_whenPasswordResetTokenFound() {
        CreatePasswordCommand createPasswordCommand = new CreatePasswordCommand("newPassword", "passwordResetToken");
        User user = User.builder().password("oldPassword").resetPasswordExpiresAt(Instant.now().plusSeconds(600)).build();
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        when(userService.findByResetPasswordToken("passwordResetToken")).thenReturn(Optional.of(user));
        when(userService.save(argument.capture())).thenReturn(user);
        when(passwordHasher.hashPassword("newPassword")).thenReturn("hashedNewPassword");

        underTest.createNewPassword(createPasswordCommand);

        assertEquals("hashedNewPassword", argument.getValue().getPassword());
        verify(userService, times(1)).findByResetPasswordToken("passwordResetToken");
        verify(userService, times(1)).save(argument.capture());
    }

    @Test
    public void createNewPassword_shouldThrowResetPasswordExpiredException_whenPasswordTokenExpired() {
        CreatePasswordCommand createPasswordCommand = new CreatePasswordCommand("newPassword", "passwordResetToken");
        User user = User.builder().password("oldPassword").resetPasswordExpiresAt(Instant.MIN).build();
        when(userService.findByResetPasswordToken("passwordResetToken")).thenReturn(Optional.of(user));

        assertThrows(ResetPasswordExpiredException.class, () -> underTest.createNewPassword(createPasswordCommand));
    }

    @Test
    public void resetPassword_shouldDoNothing_whenPasswordResetTokenNotFound() {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        underTest.resetPassword("test@example.com");

        verify(userService, times(1)).findByEmail("test@example.com");
        verify(passwordResetMessageProvider, times(0)).send(any(User.class));
        verify(userService, times(0)).save(any(User.class));
    }

    @Test
    public void resetPassword_shouldSendEmailAndSave_whenPasswordResetTokenFound() {
        User user = User.builder().email("test@example.com").build();
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        doNothing().when(passwordResetMessageProvider).send(argument.capture());
        when(userService.save(argument.capture())).thenAnswer(i -> i.<User>getArgument(0));

        underTest.resetPassword("test@example.com");

        verify(userService, times(1)).findByEmail("test@example.com");
        verify(passwordResetMessageProvider, times(1)).send(argument.capture());
        verify(userService, times(1)).save(argument.capture());
    }

    @Test
    public void changePassword_shouldUserNotFoundException_whenUserNotFound() {
        UserId userId = UserId.generate();
        ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand("newPassword", "oldPassword", userId);
        when(userService.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> underTest.changePassword(changePasswordCommand));
    }

    @Test
    public void changePassword_shouldThrowPasswordMatchException_whenOldPasswordDoesntMatch() {
        UserId userId = UserId.generate();
        User user = User.builder().email("test@example.com").password("oldPassword").build();
        ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand("newPassword", "wrongOldPassword", userId);
        when(userService.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(PasswordMatchException.class, () -> underTest.changePassword(changePasswordCommand));
    }

    @Test
    public void changePassword_shouldSaveUser_whenOldPasswordMatches() {
        UserId userId = UserId.generate();
        User user = User.builder().email("test@example.com").password("oldPassword").build();
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);

        ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand("newPassword", "oldPassword", userId);
        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(passwordHasher.verifyPassword("oldPassword", "oldPassword")).thenReturn(true);
        when(passwordHasher.hashPassword("newPassword")).thenReturn("hashedNewPassword");
        when(userService.save(argument.capture())).thenAnswer(i -> i.<User>getArgument(0));

        underTest.changePassword(changePasswordCommand);

        verify(userService, times(1)).findById(userId);
        verify(passwordHasher, times(1)).verifyPassword("oldPassword", "oldPassword");
        verify(passwordHasher, times(1)).hashPassword("newPassword");
        verify(userService, times(1)).save(argument.capture());
        assertEquals("hashedNewPassword", argument.getValue().getPassword());
    }
}
