package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.user.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.user.Lang;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.infrastructure.adapter.message.AccountCreationMessageProviderAdapter;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.configuration.SyncTestExecutorConfiguration;
import com.wilzwert.myjobs.infrastructure.utility.EmailUtility;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Tests for the AccountCreationMessageProvider
 * Warning : this test class forces sync execution, which is time-consuming but makes testing easier
 * Maybe we should learn how to effectively test async execution
 * @see SyncTestExecutorConfiguration
 *
 */
@SpringBootTest
@ActiveProfiles("integration")
@Tag("Integration")
@Import(SyncTestExecutorConfiguration.class)
public class AccountCreationMessageProviderAdapterIT extends AbstractBaseIntegrationTest {
    @MockitoSpyBean
    private JavaMailSender mailSender;

    @Autowired
    private AccountCreationMessageProviderAdapter underTest;

    @ParameterizedTest
    @CsvSource({
            "'EN', 'Account creation', 'Thank you for signing up for MyJobs'",
            "'FR', 'Création de votre compte', 'Merci de vous être inscrit à MyJobs'"
    })
    void shouldSendMail(String lang, String expectedSubject, String expectedHtml) throws Exception {
        User user = User.builder()
                .id(UserId.generate())
                .email("user@example.com")
                .emailStatus(EmailStatus.VALIDATED)
                .password("password")
                .username("user")
                .firstName("John")
                .lastName("Doe")
                .role("USER")
                .lang(Lang.valueOf(lang))
                .emailValidationCode("validation-code")
                .build();

        assertDoesNotThrow(() -> underTest.send(user));

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, atLeastOnce()).send(argument.capture());
        MimeMessage message = argument.getValue();

        assertThat(message.getSubject()).isEqualTo(expectedSubject);
        String htmlBody = EmailUtility.extractHtmlContent(message);
        assertThat(htmlBody.indexOf(expectedHtml)).isGreaterThan(-1);
        assertThat(message.getAllRecipients()[0]).hasToString("John <user@example.com>");
    }
}
