package com.wilzwert.myjobs.infrastructure.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Component
public class MailProvider {
    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    private final String frontendUrl;

    private final String from;

    private final String fromName;

    public MailProvider(
            final JavaMailSender mailSender,
            final TemplateEngine templateEngine,
            @Value("${application.frontend.url}") String frontendUrl,
            @Value("${application.mail.from}") String from,
            @Value("${application.mail.from-name}") String fromName
    ) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.frontendUrl = frontendUrl;
        this.from = from;
        this.fromName = fromName;
    }

    // TODO : improve exception handling with custom exceptions
    public CustomMailMessage createMessage(String template, String recipientMail, String recipientName, String subject)  {
        return new CustomMailMessage(template, recipientMail, recipientName, subject);
    }

    public String createUrl(String uri) {
        return frontendUrl + uri;
    }

    private MimeMessage createMimeMessage(CustomMailMessage messageToSend, String htmlContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message =  mailSender.createMimeMessage();
        System.out.println("message has been created with class " + message.getClass());
        message.setFrom(new InternetAddress(from, fromName));
        message.setRecipients(Message.RecipientType.TO, new InternetAddress(messageToSend.getRecipientMail(), messageToSend.getRecipientName()).toUnicodeString());
        message.setSubject(messageToSend.getSubject());

        // send a multipart message to allow logo embedding
        Multipart multipart = new MimeMultipart("related");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setText(htmlContent, "utf-8", "html");
        multipart.addBodyPart(htmlPart);

        try {
            MimeBodyPart imgPart = new MimeBodyPart();
            imgPart.attachFile(new ClassPathResource("static/images/logo_email.png").getFile());
            imgPart.setContentID("<logoImage>");
            multipart.addBodyPart(imgPart);
        }
        catch (IOException e) {
            // TODO ?
        }

        message.setContent(multipart);
        return message;
    }

    // TODO : improve exception handling with custom exceptions
    @Async
    public void send(CustomMailMessage messageToSend) throws MessagingException, UnsupportedEncodingException {
        Context context = new Context();
        messageToSend.getVariables().forEach(context::setVariable);
        String htmlContent = templateEngine.process(messageToSend.getTemplate(), context);
        mailSender.send(createMimeMessage(messageToSend, htmlContent));
    }
}
