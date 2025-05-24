package com.wilzwert.myjobs.infrastructure.adapter.message;

import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.PasswordResetMessageProvider;
import com.wilzwert.myjobs.infrastructure.mail.MailProvider;
import com.wilzwert.myjobs.infrastructure.mail.MailSendException;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetMessageProviderAdapter implements PasswordResetMessageProvider {
    private final MailProvider mailProvider;

    public PasswordResetMessageProviderAdapter(final MailProvider mailProvider) {
        this.mailProvider = mailProvider;
    }

    @Override
    public void send(User user) {

        try {
            var message = mailProvider.createMessage("mail/reset_password", user.getEmail(), user.getFirstName(), "email.password_reset.subject", user.getLang().toString());

            // generate URL
            String url = mailProvider.createUrl("uri.password.new", message.getLocale(), user.getResetPasswordToken());
            message.setVariable("url", url);

            mailProvider.send(message);
        }
        catch (Exception e) {
            throw new MailSendException("An error occurred while sending the mail", e);
        }

    }
}
