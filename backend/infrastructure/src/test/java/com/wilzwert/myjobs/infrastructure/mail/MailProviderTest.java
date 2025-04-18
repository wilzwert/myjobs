package com.wilzwert.myjobs.infrastructure.mail;


import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:10/04/2025
 * Time:11:12
 */
@ExtendWith(MockitoExtension.class)
public class MailProviderTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private MailProvider underTest;

    @BeforeEach
    public void setUp() {
        underTest = new MailProvider(mailSender, templateEngine, "http://frontend", "test@myjobs", "MyJobs tests");
    }

    @Test
    public void shouldCreateCustomMail() {
        CustomMailMessage message = underTest.createMessage("template", "recipient@myjobs", "MyJobs recipient", "Test subject");
        assertNotNull(message);
        assertEquals("template", message.getTemplate());
        assertEquals("recipient@myjobs", message.getRecipientMail());
        assertEquals("MyJobs recipient", message.getRecipientName());
        assertEquals("Test subject", message.getSubject());
    }

    @Test
    public void shouldSendMail() throws MessagingException, IOException {
        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        JavaMailSender mockedMailSender = spy(new JavaMailSenderImpl());
        underTest = new MailProvider(mockedMailSender, templateEngine, "http://frontend", "test@myjobs", "MyJobs tests");
        doNothing().when(mockedMailSender).send(argument.capture());
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html><body>Some html</body></html>");

        CustomMailMessage message = underTest.createMessage("template", "recipient@myjobs", "MyJobs recipient", "Test subject");
        assertNotNull(message);

        assertDoesNotThrow(() -> underTest.send(message));
        verify(mockedMailSender).send(argument.capture());

        MimeMessage mimeMessage = argument.getValue();

        // check from
        assertEquals("MyJobs tests <test@myjobs>", mimeMessage.getFrom()[0].toString());

        // check recipient
        Address[] recipients = mimeMessage.getRecipients(Message.RecipientType.TO);
        assertNotNull(recipients);
        assertEquals(1, recipients.length);
        assertEquals("MyJobs recipient <recipient@myjobs>", recipients[0].toString());

        // check subject
        assertEquals("Test subject", mimeMessage.getSubject());

        // check html body
        // Vérifier le contenu HTML du message
        MimeMultipart multipart = (MimeMultipart) mimeMessage.getContent();
        MimeBodyPart htmlPart = (MimeBodyPart) multipart.getBodyPart(0);
        String htmlContent = (String) htmlPart.getContent();
        assertEquals("<html><body>Some html</body></html>", htmlContent);
    }
}
