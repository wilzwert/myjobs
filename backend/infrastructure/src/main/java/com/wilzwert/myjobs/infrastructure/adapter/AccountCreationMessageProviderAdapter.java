package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.User;
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
            var message = mailProvider.createMessage("mail/account_creation", user.getEmail(), user.getFirstName(), "Account creation");
            // generate URL
            String url = mailProvider.createUrl("/me");
            System.out.println("Created URL "+url);
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
