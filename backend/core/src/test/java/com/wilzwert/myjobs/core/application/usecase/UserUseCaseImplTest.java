package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.EmailVerificationMessageProvider;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
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
        User user = new User.Builder().id(userId).build();

        when(userService.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(emailVerificationMessageProvider).send(user);

        underTest.sendVerificationEmail(userId);

        verify(userService, times(1)).findById(userId);
        verify(emailVerificationMessageProvider, times(1)).send(user);
    }
}
