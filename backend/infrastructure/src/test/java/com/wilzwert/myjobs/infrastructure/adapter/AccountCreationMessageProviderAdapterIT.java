package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.user.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.user.Lang;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.infrastructure.utility.EmailUtility;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("integration")
@Tag("Integration")
public class AccountCreationMessageProviderAdapterIT {
    @MockitoSpyBean
    private JavaMailSender mailSender;

    @Autowired
    private AccountCreationMessageProviderAdapter underTest;

    @ParameterizedTest
    @CsvSource({
            "'FR', 'Création de votre compte', 'Merci de vous être inscrit à MyJobs'",
            "'EN', 'Account creation', 'Thank you for signing up for MyJobs'"
    })
    public void shouldSendMail(String lang, String expectedSubject, String expectedHtml) throws Exception {
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

        // email are asynchronously sent, we have to wait to captor arguments
        CountDownLatch latch = new CountDownLatch(1);

        assertDoesNotThrow(() -> underTest.send(user));

        latch.await(5, TimeUnit.SECONDS);

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);

        verify(mailSender).send(argument.capture());
        MimeMessage message = argument.getValue();

        assertThat(message.getSubject()).isEqualTo(expectedSubject);

        String htmlBody = EmailUtility.extractHtmlContent(message);
        assertThat(htmlBody.indexOf(expectedHtml)).isGreaterThan(-1);
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo("John <user@example.com>");
    }
}
