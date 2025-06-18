package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.user.command.RegisterUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.user.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.user.Lang;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.AccountCreationMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.event.IntegrationEventPublisher;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.transaction.TransactionProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 */
@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseImplTest {

    @Mock
    private TransactionProvider transactionProvider;

    @Mock
    private IntegrationEventPublisher integrationEventPublisher;

    @Mock
    private UserDataManager userDataManager;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private AccountCreationMessageProvider accountCreationMessageProvider;

    @InjectMocks
    private RegisterUseCaseImpl underTest;

    @Test
    void whenUsernameOrEmailAlreadyExists_thenShouldThrowUserAlreadyExistsException() {
        User user = User.builder().email("test@example.com").username("username").firstName("firstName").lastName("lastName").password("Password1!").build();
        RegisterUserCommand registerUserCommand = new RegisterUserCommand("test@example.com", "password", "username",  "firstName", "lastName", null);
        when(userDataManager.findByEmailOrUsername("test@example.com", "username")).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> underTest.registerUser(registerUserCommand));
    }

    @Test
    void whenRegistrationSuccessful_thenShouldRegisterUserAndSendAccountCreationEmail() {
        ArgumentCaptor<UserCreatedEvent> userCreatedEventCaptor = ArgumentCaptor.forClass(UserCreatedEvent.class);
        RegisterUserCommand registerUserCommand = new RegisterUserCommand("test@example.com", "Password1!", "username",  "firstName", "lastName", Lang.FR);
        when(userDataManager.findByEmailOrUsername(anyString(), anyString())).thenReturn(Optional.empty());
        when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());
        when(passwordHasher.hashPassword("Password1!")).thenReturn("hashedPassword");
        when(userDataManager.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        when(integrationEventPublisher.publish(userCreatedEventCaptor.capture())).thenAnswer(i -> i.getArguments()[0]);

        User registeredUser = underTest.registerUser(registerUserCommand);
        assertNotNull(registeredUser);
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals(EmailStatus.PENDING, registeredUser.getEmailStatus());
        assertEquals("username", registeredUser.getUsername());
        assertEquals("firstName", registeredUser.getFirstName());
        assertEquals("lastName", registeredUser.getLastName());
        assertEquals("lastName", registeredUser.getLastName());
        assertEquals(User.DEFAULT_JOB_FOLLOW_UP_REMINDER_DAYS, registeredUser.getJobFollowUpReminderDays());
        assertEquals(Lang.FR, registeredUser.getLang());
        assertEquals("hashedPassword", registeredUser.getPassword());

        verify(userDataManager, times(1)).findByEmailOrUsername("test@example.com", "username");
        verify(userDataManager, times(1)).save(any(User.class));
        verify(accountCreationMessageProvider, times(1)).send(any(User.class));
        verify(transactionProvider, times(1)).executeInTransaction(any(Supplier.class));
        verify(passwordHasher, times(1)).hashPassword("Password1!");
        verify(integrationEventPublisher, times(1)).publish(userCreatedEventCaptor.capture());

        UserCreatedEvent userCreatedEvent = userCreatedEventCaptor.getValue();
        assertNotNull(userCreatedEvent);
        assertEquals(registeredUser.getId(), userCreatedEvent.getUserId());
    }

    @Test
    void whenEmailIsTaken_thenShouldReturnTrue() {
        when(userDataManager.emailExists(anyString())).thenReturn(true);

        assertTrue(underTest.isEmailTaken("test@example.com"));
    }

    @Test
    void whenEmailIsAvailable_thenShouldReturnFalse() {
        when(userDataManager.emailExists(anyString())).thenReturn(false);

        assertFalse(underTest.isEmailTaken("test@example.com"));
    }


    @Test
    void whenUsernameIsTaken_thenShouldReturnTrue() {
        when(userDataManager.usernameExists(anyString())).thenReturn(true);

        assertTrue(underTest.isUsernameTaken("test"));

    }

    @Test
    void whenUsernameIsAvailable_thenShouldReturnFalse() {
        when(userDataManager.usernameExists(anyString())).thenReturn(false);

        assertFalse(underTest.isUsernameTaken("test"));
    }
}