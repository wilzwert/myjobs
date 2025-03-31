package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordResetMessageProvider;
import com.wilzwert.myjobs.infrastructure.mail.MailProvider;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

@Component
public class PasswordResetMessageProviderAdapter implements PasswordResetMessageProvider {
    private final MailProvider mailProvider;

    public PasswordResetMessageProviderAdapter(final MailProvider mailProvider) {
        this.mailProvider = mailProvider;
    }

    @Override
    public void send(User user) {

        try {
            var message = mailProvider.createMessage("mail/reset_password", user.getEmail(), user.getFirstName(), "Password reset");

            // generate URL
            String url = mailProvider.createUrl("/password/new?token=" + user.getResetPasswordToken());
            message.setVariable("url", url);

            mailProvider.send(message);
        }
        // TODO : improve exception handling
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
