package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.user.EmailStatus;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ValidateEmailRequest;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.configuration.SyncTestExecutorConfiguration;
import com.wilzwert.myjobs.infrastructure.security.service.JwtService;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Import(SyncTestExecutorConfiguration.class)
class EmailControllerIT extends AbstractBaseIntegrationTest {
    private static final String USER_FOR_EMAIL_VALIDATION_TEST_ID = "abcd4321-4321-4321-4321-123456789012";

    @MockitoSpyBean
    private JavaMailSender mailSender;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDataManager userDataManager;

    @Autowired
    private JwtService jwtService;

    private Cookie accessTokenCookie;

    @BeforeEach
    void setUp()  {
       accessTokenCookie = new Cookie("access_token", jwtService.generateToken(USER_FOR_EMAIL_VALIDATION_TEST_ID));
    }

    private static final String MAIL_OPERATION_URL = "/api/user/me/email";

    @Nested
    class EmailVerificationIT {

        @Test
        void whenUserNotAuthenticated_thenVerificationShouldReturnUnauthenticated() throws Exception {
            mockMvc.perform(get(MAIL_OPERATION_URL + "/verification"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void whenUnsupportedMethod_thenVerificationShouldReturnUnsupported() throws Exception {
            mockMvc.perform(get(MAIL_OPERATION_URL + "/verification").cookie(accessTokenCookie))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        void whenUserNotFound_thenShouldReturnUnauthorized() throws Exception {
            Cookie notExistentUserCookie = new Cookie("access_token", jwtService.generateToken("aaaa"));
            mockMvc.perform(post(MAIL_OPERATION_URL + "/verification").cookie(notExistentUserCookie))
                    .andExpect(status().isUnauthorized());
        }


        @Test
        void shouldSendVerificationEmail() throws Exception {
            mockMvc.perform(post(MAIL_OPERATION_URL + "/verification").cookie(accessTokenCookie))
                    .andExpect(status().isOk());

            ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
            verify(mailSender, atLeastOnce()).send(argument.capture());
            MimeMessage message = argument.getValue();

            assertThat(message.getSubject()).isEqualTo("Email verification");
        }
    }

    @Nested
    class EmailValidationIT {
        @Test
        void whenUnsupportedMethod_thenVerificationShouldReturnUnsupported() throws Exception {
            mockMvc.perform(get(MAIL_OPERATION_URL + "/validation"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        void whenUserNotFound_thenShouldReturnNotFound() throws Exception {
            ValidateEmailRequest request = new ValidateEmailRequest();
            request.setCode("non-existent");
            mockMvc.perform(post(MAIL_OPERATION_URL + "/validation").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }


        @Test
        void shouldValidateEmail() throws Exception {
            // get original user for later "rollback"
            User original = userDataManager.findById(new UserId(UUID.fromString(USER_FOR_EMAIL_VALIDATION_TEST_ID))).get();
            assertThat(original.getEmailStatus()).isEqualTo(EmailStatus.PENDING);

            ValidateEmailRequest request = new ValidateEmailRequest();
            request.setCode("other-existing-email-validation-code");
            mockMvc.perform(post(MAIL_OPERATION_URL + "/validation").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            User updated = userDataManager.findById(new UserId(UUID.fromString(USER_FOR_EMAIL_VALIDATION_TEST_ID))).get();
            assertThat(updated.getEmailStatus()).isEqualTo(EmailStatus.VALIDATED);

            // resets original stated
            userDataManager.save(original);
        }
    }
}
