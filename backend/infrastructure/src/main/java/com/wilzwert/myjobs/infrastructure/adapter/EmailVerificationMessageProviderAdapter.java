package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.ports.driven.EmailVerificationMessageProvider;
import com.wilzwert.myjobs.infrastructure.mail.MailProvider;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationMessageProviderAdapter implements EmailVerificationMessageProvider {

    private final MailProvider mailProvider;

    public EmailVerificationMessageProviderAdapter(final MailProvider mailProvider) {
        this.mailProvider = mailProvider;
    }

    @Override
    public void send(User user) {
        try {
            var message = mailProvider.createMessage("mail/email_verification", user.getEmail(), user.getFirstName(), "Email verification");
            // generate URL
            String url = mailProvider.createUrl("/me");
            message.setVariable("url", url);
            message.setVariable("firstName", user.getFirstName());
            message.setVariable("lastName", user.getLastName());
            message.setVariable("validationUrl", mailProvider.createUrl("/me/email/validation?code="+user.getEmailValidationCode()));
            mailProvider.send(message);
        }
        // TODO : improve exception handling
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
