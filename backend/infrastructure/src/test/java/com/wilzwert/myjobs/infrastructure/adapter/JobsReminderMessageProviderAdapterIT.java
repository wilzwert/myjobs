package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.user.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.user.Lang;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.infrastructure.adapter.message.JobReminderMessageProviderAdapter;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/05/2025
 * Time:15:50
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
public class JobsReminderMessageProviderAdapterIT extends AbstractBaseIntegrationTest {
    @MockitoSpyBean
    private JavaMailSender mailSender;

    @Autowired
    private JobReminderMessageProviderAdapter underTest;

    @ParameterizedTest
    @CsvSource({
            "'EN', 'Late follow-up jobs', 'Some jobs require your attention, as they had no activity in the past 7 days'",
            "'FR', 'Jobs à mettre à jour', 'Certains jobs demandent votre attention : il n&#39;ont eu aucune activité depuis 7 jours ou plus.'"
    })
    public void shouldSendMail(String lang, String expectedSubject, String expectedHtml) throws Exception {
        UserId userId = UserId.generate();
        Set<Job> jobs = new HashSet<>(List.of(
            Job.builder()
                .userId(userId)
                .url("http://www.example.com/1")
                .status(JobStatus.PENDING)
                .title("Job 1")
                .company("Company")
                .description("Job 1 description")
                .build()
        ));

        User user = User.builder()
                .id(userId)
                .email("user@example.com")
                .emailStatus(EmailStatus.VALIDATED)
                .password("password")
                .username("user")
                .firstName("John")
                .lastName("Doe")
                .role("USER")
                .lang(Lang.valueOf(lang))
                .emailValidationCode("validation-code")
                .jobFollowUpReminderDays(7)
                .build();

        assertDoesNotThrow(() -> underTest.send(user, jobs));

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, atLeastOnce()).send(argument.capture());
        MimeMessage message = argument.getValue();

        assertThat(message.getSubject()).isEqualTo(expectedSubject);
        String htmlBody = EmailUtility.extractHtmlContent(message);
        assertThat(htmlBody.indexOf(expectedHtml)).isGreaterThan(-1);
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo("John <user@example.com>");
    }
}
