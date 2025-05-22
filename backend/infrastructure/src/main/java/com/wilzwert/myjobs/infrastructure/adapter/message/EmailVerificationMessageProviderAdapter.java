package com.wilzwert.myjobs.infrastructure.adapter.message;

import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.EmailVerificationMessageProvider;
import com.wilzwert.myjobs.infrastructure.mail.MailProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailVerificationMessageProviderAdapter implements EmailVerificationMessageProvider {

    private final MailProvider mailProvider;

    public EmailVerificationMessageProviderAdapter(final MailProvider mailProvider) {
        this.mailProvider = mailProvider;
    }

    @Override
    public void send(User user) {
        try {
            var message = mailProvider.createMessage("mail/email_verification", user.getEmail(), user.getFirstName(), "email.email_verification.subject", user.getLang().toString());
            message.setVariable("url", mailProvider.createMeUrl(message.getLocale()));
            message.setVariable("firstName", user.getFirstName());
            message.setVariable("lastName", user.getLastName());
            message.setVariable("validationUrl", mailProvider.createUrl("uri.email_validation", message.getLocale(), user.getEmailValidationCode()));
            log.debug("Sending email verification message : {}", user.getEmail());
            log.debug("Class of mailProvider: {}", mailProvider.getClass());
            mailProvider.send(message);
            log.debug("Email verification message should have been send");
        }
        // TODO : improve exception handling
        catch (Exception e) {
            log.error("Sending email verification message failed.", e);
            throw new RuntimeException(e);
        }
    }
}
