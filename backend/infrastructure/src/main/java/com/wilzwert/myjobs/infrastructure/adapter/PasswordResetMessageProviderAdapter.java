package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordResetMessageProvider;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class PasswordResetMessageProviderAdapter implements PasswordResetMessageProvider {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @Value("${application.frontend.url}")
    private String frontendUrl;

    @Value("${application.mail.from}")
    private String from;

    @Value("${application.mail.from-name}")
    private String fromName;

    public PasswordResetMessageProviderAdapter(final JavaMailSender mailSender, final TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void send(User user) {
        // generate URL
        String url = frontendUrl + "/password/new?token=" + user.getResetPasswordToken();

        try {
            var message = mailSender.createMimeMessage();
            message.setFrom(new InternetAddress(from, fromName));
            message.setRecipients(MimeMessage.RecipientType.TO, new InternetAddress(user.getEmail(), user.getFirstName()).getAddress());
            message.setSubject("Password reset");

            Context context = new Context();
            context.setVariable("url", url);
            String htmlContent = templateEngine.process("mail/reset_password", context);

            message.setContent(htmlContent, "text/html;charset=utf-8");
            mailSender.send(message);
        }
        // TODO : improve exception handling
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
