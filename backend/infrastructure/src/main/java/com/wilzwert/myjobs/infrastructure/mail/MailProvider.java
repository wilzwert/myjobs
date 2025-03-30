package com.wilzwert.myjobs.infrastructure.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class MailProvider {
    private final JavaMailSender mailSender;

    @Value("${application.frontend.url}")
    private String frontendUrl;

    @Value("${application.mail.from}")
    private String from;

    @Value("${application.mail.from-name}")
    private String fromName;

    public MailProvider(final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // TODO : improve exception handling with custom exceptions
    public MimeMessage createMessage(String recipientMail, String recipientName, String subject) throws MessagingException, UnsupportedEncodingException {
        var message =  mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(recipientMail, recipientName));
        message.setRecipients(Message.RecipientType.TO, new InternetAddress(recipientMail, recipientName).toUnicodeString());
        message.setSubject(subject);
        return message;
    }

    public String createUrl(String uri) {
        return frontendUrl + uri;
    }

    // TODO : improve exception handling with custom exceptions
    public void send(MimeMessage message) throws MessagingException, UnsupportedEncodingException {
        mailSender.send(message);
    }
}
