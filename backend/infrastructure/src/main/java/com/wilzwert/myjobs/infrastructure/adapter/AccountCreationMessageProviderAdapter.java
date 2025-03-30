package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.ports.driven.AccountCreationMessageProvider;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordResetMessageProvider;
import com.wilzwert.myjobs.infrastructure.mail.MailProvider;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class AccountCreationMessageProviderAdapter implements AccountCreationMessageProvider {

    private final MailProvider mailProvider;

    private final TemplateEngine templateEngine;

    public AccountCreationMessageProviderAdapter(final MailProvider mailProvider, final TemplateEngine templateEngine) {
        this.mailProvider = mailProvider;
        this.templateEngine = templateEngine;
    }

    @Override
    public void send(User user) {
        try {
            var message = mailProvider.createMessage(user.getEmail(), user.getFirstName(), "Account creation");
            // generate URL
            String url = mailProvider.createUrl("/me");
            Context context = new Context();
            context.setVariable("url", url);
            context.setVariable("firstName", user.getFirstName());
            context.setVariable("lastName", user.getLastName());
            context.setVariable("validationUrl", mailProvider.createUrl("/me/email/validation?code="+user.getEmailValidationCode()));
            String htmlContent = templateEngine.process("mail/account_creation", context);
            message.setContent(htmlContent, "text/html;charset=utf-8");
            mailProvider.send(message);
        }
        // TODO : improve exception handling
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
