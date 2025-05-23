package com.wilzwert.myjobs.infrastructure.adapter.message;

import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.AccountCreationMessageProvider;
import com.wilzwert.myjobs.infrastructure.mail.MailProvider;
import com.wilzwert.myjobs.infrastructure.mail.MailSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountCreationMessageProviderAdapter implements AccountCreationMessageProvider {

    private final MailProvider mailProvider;

    public AccountCreationMessageProviderAdapter(final MailProvider mailProvider) {
        this.mailProvider = mailProvider;
    }

    @Override
    public void send(User user) {
        try {
            var message = mailProvider.createMessage("mail/account_creation", user.getEmail(), user.getFirstName(), "email.account_creation.subject", user.getLang().toString());
            message.setVariable("url", mailProvider.createMeUrl(message.getLocale()));
            message.setVariable("firstName", user.getFirstName());
            message.setVariable("lastName", user.getLastName());
            message.setVariable("validationUrl", mailProvider.createUrl("uri.email_validation", message.getLocale(), user.getEmailValidationCode()));
            log.info("Sending account creation message: {}", user.getEmail());
            mailProvider.send(message);
        }
        catch (Exception e) {
            log.info("Sending account creation message failed.", e);
            throw new MailSendException("Sending account creation message failed.", e);
        }
    }
}