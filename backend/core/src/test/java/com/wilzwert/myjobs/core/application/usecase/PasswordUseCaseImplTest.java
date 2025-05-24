package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.user.command.ChangePasswordCommand;
import com.wilzwert.myjobs.core.domain.model.user.command.CreatePasswordCommand;
import com.wilzwert.myjobs.core.domain.model.user.exception.PasswordMatchException;
import com.wilzwert.myjobs.core.domain.model.user.exception.ResetPasswordExpiredException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.PasswordResetMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 */
@ExtendWith(MockitoExtension.class)
class PasswordUseCaseImplTest {
    @Mock
    private UserDataManager userDataManager;

    @Mock
    private PasswordResetMessageProvider passwordResetMessageProvider;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private PasswordUseCaseImpl underTest;

    @Test
    void whenPasswordResetTokenNotFound_thenCreateNewPasswordShouldDoNothing_() {
        CreatePasswordCommand createPasswordCommand = new CreatePasswordCommand("newPassword", "passwordResetToken");
        when(userDataManager.findByResetPasswordToken("passwordResetToken")).thenReturn(Optional.empty());

        underTest.createNewPassword(createPasswordCommand);

        verify(userDataManager, times(1)).findByResetPasswordToken("passwordResetToken");
        verify(passwordHasher, times(0)).hashPassword(anyString());
        verify(userDataManager, times(0)).save(any(User.class));


    }

    @Test
    void whenPasswordResetTokenFound_thenCreateNewPasswordShouldSaveUser_() {
        CreatePasswordCommand createPasswordCommand = new CreatePasswordCommand("newPassword1!", "passwordResetToken");
        User user = User.builder()
                .email("test@example.com")
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .password("OldPassword1!")
                .resetPasswordExpiresAt(Instant.now().plusSeconds(600))
                .jobs(Collections.emptyList())
                .build();
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        when(userDataManager.findByResetPasswordToken("passwordResetToken")).thenReturn(Optional.of(user));
        when(userDataManager.save(argument.capture())).thenReturn(user);
        when(passwordHasher.hashPassword("newPassword1!")).thenReturn("hashedNewPassword");

        underTest.createNewPassword(createPasswordCommand);

        assertEquals("hashedNewPassword", argument.getValue().getPassword());
        verify(userDataManager, times(1)).findByResetPasswordToken("passwordResetToken");
        verify(userDataManager, times(1)).save(argument.capture());
    }

    @Test
    void whenPasswordTokenExpired_thenCreateNewPasswordShouldThrowResetPasswordExpiredException_() {
        CreatePasswordCommand createPasswordCommand = new CreatePasswordCommand("NewPassword1!", "passwordResetToken");
        User user = User.builder()
                .email("test@example.com")
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .password("OldPassword1!")
                .resetPasswordExpiresAt(Instant.MIN)
                .build();

        when(userDataManager.findByResetPasswordToken("passwordResetToken")).thenReturn(Optional.of(user));

        assertThrows(ResetPasswordExpiredException.class, () -> underTest.createNewPassword(createPasswordCommand));
    }

    @Test
    void whenPasswordResetTokenNotFound_thenResetPasswordShouldDoNothing() {
        when(userDataManager.findByEmail("test@example.com")).thenReturn(Optional.empty());

        underTest.resetPassword("test@example.com");

        verify(userDataManager, times(1)).findByEmail("test@example.com");
        verify(passwordResetMessageProvider, times(0)).send(any(User.class));
        verify(userDataManager, times(0)).save(any(User.class));
    }

    @Test
    void whenPasswordResetTokenFound_thenResetPasswordShouldSendEmailAndSave_() {
        User user = User.builder()
                .email("test@example.com")
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .password("OldPassword1!")
                .resetPasswordExpiresAt(Instant.MIN)
                .jobs(Collections.emptyList())
                .build();
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        when(userDataManager.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        doNothing().when(passwordResetMessageProvider).send(argument.capture());
        when(userDataManager.save(argument.capture())).thenAnswer(i -> i.<User>getArgument(0));

        underTest.resetPassword("test@example.com");

        verify(userDataManager, times(1)).findByEmail("test@example.com");
        verify(passwordResetMessageProvider, times(1)).send(argument.capture());
        verify(userDataManager, times(1)).save(argument.capture());
    }

    @Test
    void whenUserNotFound_thenChangePasswordShouldUserNotFoundException_() {
        UserId userId = UserId.generate();
        ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand("newPassword", "oldPassword", userId);
        when(userDataManager.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> underTest.changePassword(changePasswordCommand));
    }

    @Test
    void whenOldPasswordDoesntMatch_thenChangePasswordShouldThrowPasswordMatchException_() {
        UserId userId = UserId.generate();
        User user = User.builder()
                .email("test@example.com")
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .password("OldPassword1!")
                .resetPasswordExpiresAt(Instant.MIN)
                .build();
        ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand("newPassword", "wrongOldPassword", userId);
        when(userDataManager.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(PasswordMatchException.class, () -> underTest.changePassword(changePasswordCommand));
    }

    @Test
    void whenOldPasswordMatches_thenChangePasswordShouldSaveUser() {
        UserId userId = UserId.generate();
        User user = User.builder()
                .email("test@example.com")
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .password("OldPassword1!")
                .resetPasswordExpiresAt(Instant.MIN)
                .jobs(Collections.emptyList())
                .build();
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);

        ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand("newPassword1!", "OldPassword1!", userId);
        when(userDataManager.findById(userId)).thenReturn(Optional.of(user));
        when(passwordHasher.verifyPassword("OldPassword1!", "OldPassword1!")).thenReturn(true);
        when(passwordHasher.hashPassword("newPassword1!")).thenReturn("hashedNewPassword");
        when(userDataManager.save(argument.capture())).thenAnswer(i -> i.<User>getArgument(0));

        underTest.changePassword(changePasswordCommand);

        verify(userDataManager, times(1)).findById(userId);
        verify(passwordHasher, times(1)).verifyPassword("OldPassword1!", "OldPassword1!");
        verify(passwordHasher, times(1)).hashPassword("newPassword1!");
        verify(userDataManager, times(1)).save(argument.capture());
        assertEquals("hashedNewPassword", argument.getValue().getPassword());
    }
}
