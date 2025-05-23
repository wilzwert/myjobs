package com.wilzwert.myjobs.infrastructure.mail;


import com.wilzwert.myjobs.infrastructure.exception.MailSendException;
import com.wilzwert.myjobs.infrastructure.storage.SecureTempFileHelper;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    private MailProperties mailProperties;

    private SecureTempFileHelper secureTempFileHelper;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MessageSource messageSource;



    private MailProvider underTest;

    @BeforeEach
    public void setUp() throws Exception {
        secureTempFileHelper = new SecureTempFileHelper();
        mailProperties = new MailProperties();
        mailProperties.setFrom("test@myjobs");
        mailProperties.setFromName("MyJobs tests");
        underTest = new MailProvider(mailSender, templateEngine, messageSource, secureTempFileHelper, mailProperties, "http://frontend",  "EN");
        underTest.init();
    }

    @Test
    void shouldCopyImageToTempFileOnInit() throws Exception {
        File logoFile = underTest.getLogoTempFile();
        assertNotNull(logoFile);
        assertTrue(logoFile.exists());
        assertTrue(logoFile.length() > 0);

        try (
                InputStream original = new ClassPathResource("static/images/logo_email.png").getInputStream();
                InputStream copied = new FileInputStream(logoFile)
        ) {
            byte[] originalBytes = original.readAllBytes();
            byte[] copiedBytes = copied.readAllBytes();
            assertArrayEquals(originalBytes, copiedBytes);
        }
    }

    @Test
    public void shouldCreateCustomMail() {
        CustomMailMessage message = underTest.createMessage("template", "recipient@myjobs", "MyJobs recipient", "Test subject", "EN");
        assertNotNull(message);
        assertEquals("template", message.getTemplate());
        assertEquals("recipient@myjobs", message.getRecipientMail());
        assertEquals("MyJobs recipient", message.getRecipientName());
        assertEquals("Test subject", message.getSubject());
        assertEquals("EN", message.getLang());

    }


    @Test
    public void shouldSendMail() throws MessagingException, IOException {
        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        JavaMailSender mockedMailSender = spy(new JavaMailSenderImpl());

        underTest = new MailProvider(mockedMailSender, templateEngine, messageSource, secureTempFileHelper, mailProperties, "http://frontend", "EN");
        underTest.init();
        doNothing().when(mockedMailSender).send(argument.capture());

        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html><body>Some html</body></html>");
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Subject from message source");

        CustomMailMessage message = underTest.createMessage("template", "recipient@myjobs", "MyJobs recipient", "Test subject", "EN");
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
        assertEquals("Subject from message source", mimeMessage.getSubject());

        // check html body
        // Vérifier le contenu HTML du message
        MimeMultipart multipart = (MimeMultipart) mimeMessage.getContent();
        MimeBodyPart htmlPart = (MimeBodyPart) multipart.getBodyPart(0);
        String htmlContent = (String) htmlPart.getContent();
        assertEquals("<html><body>Some html</body></html>", htmlContent);
    }

    @Test
    void should_throwRuntimeException_whenSendingFails()  {
        CustomMailMessage message = new CustomMailMessage("template", "test@test.com", "User", "subject.key", "fr");
        message.setVariables(new HashMap<>());

        when(templateEngine.process(eq("template"), any(Context.class))).thenReturn("<html>content</html>");
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Cannot create message"));

        assertThrows(RuntimeException.class, () -> underTest.send(message));
    }

    @Test
    void whenMailSenderThrowsMessagingException_thenShouldThrowMailSendException() throws IOException {
        JavaMailSender mockedMailSender = spy(new JavaMailSenderImpl());

        underTest = new MailProvider(mockedMailSender, templateEngine, messageSource, secureTempFileHelper, mailProperties, "http://frontend", "EN");
        underTest.init();

        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html><body>Some html</body></html>");
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Subject from message source");

        doThrow(new org.springframework.mail.MailSendException("error", new Exception())).when(mockedMailSender).send(any(MimeMessage.class));

        CustomMailMessage message = underTest.createMessage("template", "recipient@myjobs", "MyJobs recipient", "Test subject", "EN");
        assertNotNull(message);

        var ex = assertThrows(MailSendException.class, () -> underTest.send(message));
        assertThat(ex.getMessage()).isEqualTo("Unable to send message");
        verify(mockedMailSender).send(any(MimeMessage.class));
    }

    @Test
    void whenMailSenderThrowsUnsupportedEncodingException_thenShouldThrowMailSendException() throws IOException {
        JavaMailSender mockedMailSender = spy(new JavaMailSenderImpl());

        underTest = new MailProvider(mockedMailSender, templateEngine, messageSource, secureTempFileHelper, mailProperties, "http://frontend", "EN");
        underTest.init();

        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html><body>Some html</body></html>");
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Subject from message source");

        doThrow(new RuntimeException()).when(mockedMailSender).send(any(MimeMessage.class));

        CustomMailMessage message = underTest.createMessage("template", "recipient@myjobs", "MyJobs recipient", "Test subject", "EN");
        assertNotNull(message);

        var ex = assertThrows(MailSendException.class, () -> underTest.send(message));
        assertThat(ex.getMessage()).isEqualTo("Unexpected exception while building or sending the message");
        verify(mockedMailSender).send(any(MimeMessage.class));
    }


}
