package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.infrastructure.mail.CustomMailMessage;
import com.wilzwert.myjobs.infrastructure.mail.MailProvider;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordResetMessageProviderAdapterTest {

    private PasswordResetMessageProviderAdapter underTest;

    @Mock
    private MailProvider mailProvider;

    @BeforeEach
    public void setUp() {
        underTest = new PasswordResetMessageProviderAdapter(mailProvider);
    }

    @Test
    public void testSendEmail() throws MessagingException, UnsupportedEncodingException {
        CustomMailMessage mailMessage = new CustomMailMessage("mail/reset_password", "user@example.com", "John", "Password reset");
        when(mailProvider.createMessage("mail/reset_password", "user@example.com", "John", "Password reset")).thenReturn(mailMessage);
        when(mailProvider.createUrl("/password/new?token=reset-token")).thenReturn("http://myjobs.com/password/new?token=reset-token")  ;

        User user = User.builder()
                .id(UserId.generate())
                .email("user@example.com")
                .emailStatus(EmailStatus.VALIDATED)
                .password("password")
                .username("user")
                .firstName("John")
                .lastName("Doe")
                .role("USER")
                .resetPasswordToken("reset-token")
                .build();

        assertDoesNotThrow(() -> underTest.send(user));
        verify(mailProvider).createMessage("mail/reset_password", "user@example.com", "John", "Password reset");
        verify(mailProvider).createUrl("/password/new?token=reset-token");
        verify(mailProvider).send(mailMessage);

        assertThat(mailMessage.getVariables())
                .hasSize(1)
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        "url", "http://myjobs.com/password/new?token=reset-token"

                ));
    }
}