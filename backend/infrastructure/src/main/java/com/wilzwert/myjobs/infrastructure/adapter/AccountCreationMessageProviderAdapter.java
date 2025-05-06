package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.ports.driven.AccountCreationMessageProvider;
import com.wilzwert.myjobs.infrastructure.mail.MailProvider;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class AccountCreationMessageProviderAdapter implements AccountCreationMessageProvider {

    private final MailProvider mailProvider;

    public AccountCreationMessageProviderAdapter(final MailProvider mailProvider) {
        this.mailProvider = mailProvider;
    }

    @Override
    public void send(User user) {
        try {
            var message = mailProvider.createMessage("mail/account_creation", user.getEmail(), user.getFirstName(), "email.account_creation.subject", user.getLang().toString());
            // generate URL
            // TODO : localize urls ?
            String url = mailProvider.createUrl("/me");
            message.setVariable("url", url);
            message.setVariable("firstName", user.getFirstName());
            message.setVariable("lastName", user.getLastName());
            message.setVariable("validationUrl", mailProvider.createUrl("/me/email/validation?code="+user.getEmailValidationCode()));
            mailProvider.send(message);
        }
        // TODO : improve exception handling
        catch (MessagingException |UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }
}
