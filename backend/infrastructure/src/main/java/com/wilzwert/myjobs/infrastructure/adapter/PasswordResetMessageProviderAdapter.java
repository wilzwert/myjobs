package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordResetMessageProvider;
import com.wilzwert.myjobs.infrastructure.mail.MailProvider;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class PasswordResetMessageProviderAdapter implements PasswordResetMessageProvider {
    private final MailProvider mailProvider;

    private final TemplateEngine templateEngine;

    public PasswordResetMessageProviderAdapter(final MailProvider mailProvider, final TemplateEngine templateEngine) {
        this.mailProvider = mailProvider;
        this.templateEngine = templateEngine;
    }

    @Override
    public void send(User user) {

        try {
            var message = mailProvider.createMessage(user.getEmail(), user.getFirstName(), "Password reset");

            // generate URL
            String url = mailProvider.createUrl("/password/new?token=" + user.getResetPasswordToken());
            Context context = new Context();
            context.setVariable("url", url);
            String htmlContent = templateEngine.process("mail/reset_password", context);

            message.setContent(htmlContent, "text/html;charset=utf-8");
            mailProvider.send(message);
        }
        // TODO : improve exception handling
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
