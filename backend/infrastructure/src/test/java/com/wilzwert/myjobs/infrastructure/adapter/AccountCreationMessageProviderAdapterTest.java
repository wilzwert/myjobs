package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.user.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.user.Lang;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.infrastructure.mail.CustomMailMessage;
import com.wilzwert.myjobs.infrastructure.mail.MailProvider;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountCreationMessageProviderAdapterTest {

    private AccountCreationMessageProviderAdapter underTest;

    @Mock
    private MailProvider mailProvider;

    @BeforeEach
    public void setUp() {
        underTest = new AccountCreationMessageProviderAdapter(mailProvider);
    }

    @Test
    public void testSendEmail() throws MessagingException, UnsupportedEncodingException {
        Locale locale = Locale.of(Lang.EN.name());
        CustomMailMessage mailMessage = new CustomMailMessage("mail/account_creation", "user@example.com", "John", "email.account_creation.subject", "EN");
        when(mailProvider.createMessage("mail/account_creation", "user@example.com", "John", "email.account_creation.subject", "EN")).thenReturn(mailMessage);
        when(mailProvider.createMeUrl(locale)).thenReturn("http://myjobs.com/me");
        when(mailProvider.createUrl("uri.email_validation", locale, "validation-code")).thenReturn("http://myjobs.com/me/email/validation?code=validation-code")  ;

        User user = User.builder()
                .id(UserId.generate())
                .email("user@example.com")
                .emailStatus(EmailStatus.VALIDATED)
                .password("password")
                .username("user")
                .firstName("John")
                .lastName("Doe")
                .role("USER")
                .lang(Lang.EN)
                .emailValidationCode("validation-code")
                .build();

        assertDoesNotThrow(() -> underTest.send(user));
        verify(mailProvider).createMessage("mail/account_creation", "user@example.com", "John", "email.account_creation.subject", "EN");
        verify(mailProvider).createMeUrl(locale);
        verify(mailProvider).send(mailMessage);

        assertThat(mailMessage.getVariables())
                .hasSize(4)
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        "url", "http://myjobs.com/me",
                        "firstName", "John",
                        "lastName", "Doe",
                        "validationUrl", "http://myjobs.com/me/email/validation?code=validation-code"

                ));
    }
}
