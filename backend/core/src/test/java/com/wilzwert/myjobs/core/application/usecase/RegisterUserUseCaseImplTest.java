package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.user.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.ports.driven.AccountCreationMessageProvider;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:09/04/2025
 * Time:09:16
 */
@ExtendWith(MockitoExtension.class)
public class RegisterUserUseCaseImplTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private AccountCreationMessageProvider accountCreationMessageProvider;

    @InjectMocks
    private RegisterUseCaseImpl underTest;

    @Test
    public void shouldThrowUserAlreadyExistsException_whenUsernameOrEmailAlreadyExists() {
        User user = User.builder().username("username").build();
        RegisterUserCommand registerUserCommand = new RegisterUserCommand("test@example.com", "password", "username",  "firstName", "lastName");
        when(userService.findByEmailOrUsername("test@example.com", "username")).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> underTest.registerUser(registerUserCommand));
    }

    @Test
    public void shouldRegisterUserAndSendAccountCreationEmail_whenRegistrationSuccessful() {
        RegisterUserCommand registerUserCommand = new RegisterUserCommand("test@example.com", "password", "username",  "firstName", "lastName");
        when(userService.findByEmailOrUsername(anyString(), anyString())).thenReturn(Optional.empty());
        when(passwordHasher.hashPassword("password")).thenReturn("hashedPassword");
        when(userService.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);


        User registeredUser = underTest.registerUser(registerUserCommand);
        assertNotNull(registeredUser);
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals(EmailStatus.PENDING, registeredUser.getEmailStatus());
        assertEquals("username", registeredUser.getUsername());
        assertEquals("firstName", registeredUser.getFirstName());
        assertEquals("lastName", registeredUser.getLastName());
        assertEquals("hashedPassword", registeredUser.getPassword());

        verify(userService, times(1)).findByEmailOrUsername("test@example.com", "username");
        verify(userService, times(1)).save(any(User.class));
        verify(accountCreationMessageProvider, times(1)).send(any(User.class));
    }

    @Test
    public void shouldReturnTrue_whenEmailIsTaken() {
        when(userService.emailExists(anyString())).thenReturn(true);

        assertTrue(underTest.isEmailTaken("test@example.com"));
    }

    @Test
    public void shouldReturnFalse_whenEmailIsAvailable() {
        when(userService.emailExists(anyString())).thenReturn(false);

        assertFalse(underTest.isEmailTaken("test@example.com"));
    }


    @Test
    public void shouldReturnTrue_whenUsernameIsTaken() {
        when(userService.usernameExists(anyString())).thenReturn(true);

        assertTrue(underTest.isUsernameTaken("test"));

    }

    @Test
    public void shouldReturnFalse_whenUsernameIsAvailable() {
        when(userService.usernameExists(anyString())).thenReturn(false);

        assertFalse(underTest.isUsernameTaken("test"));
    }
}
