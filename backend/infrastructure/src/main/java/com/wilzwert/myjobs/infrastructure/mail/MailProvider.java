package com.wilzwert.myjobs.infrastructure.mail;

import com.wilzwert.myjobs.infrastructure.storage.SecureTempFileHelper;
import jakarta.annotation.PostConstruct;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.nio.file.Files;
import java.util.Locale;

@Component
@Slf4j
public class MailProvider {
    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    /**
     * The MessageSource used for translations
     * This includes frontend URIs which could become localized in a near future
     */
    private final MessageSource messageSource;

    private final SecureTempFileHelper secureTempFileHelper;

    private final String frontendUrl;

    private final String from;

    private final String fromName;

    private final String defaultLanguage;

    private File logoTempFile;



    public MailProvider(
            final JavaMailSender mailSender,
            final TemplateEngine templateEngine,
            final MessageSource messageSource,
            final SecureTempFileHelper secureTempFileHelper,
            final MailProperties mailProperties,
            @Value("${application.frontend.url}") String frontendUrl,
            @Value("${application.default-language}") String defaultLanguage
    ) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
        this.secureTempFileHelper = secureTempFileHelper;
        this.frontendUrl = frontendUrl;
        this.from = mailProperties.getFrom();
        this.fromName = mailProperties.getFromName();
        this.defaultLanguage = defaultLanguage;
    }

    @PostConstruct
    public void init() throws IOException {
        log.info("Copying logo to tmp file");
        // copy images to tmp files at startup
        ClassPathResource imgResource = new ClassPathResource("static/images/logo_email.png");
        logoTempFile = Files.createTempFile("temp", ".png", secureTempFileHelper.getFileAttribute()).toFile();
        if(!logoTempFile.exists()) {
            throw new IOException("Could not find logo");
        }

        logoTempFile.deleteOnExit(); // cleanup on jvm exit
        try (InputStream in = imgResource.getInputStream(); FileOutputStream out = new FileOutputStream(logoTempFile)) {
            in.transferTo(out);
        }
    }

    File getLogoTempFile() {
        return logoTempFile;
    }

    public CustomMailMessage createMessage(String template, String recipientMail, String recipientName, String subject, String lang)  {
        return new CustomMailMessage(template, recipientMail, recipientName, subject, (lang != null ? lang : defaultLanguage));
    }

    /**
     *
     * Creates a URL based on the uri and the locale provided
     * URI MUST be an entry in the MessageSource (i.e. resources/localization/messages[_lang].properties
     * otherwise it will become unpredictable, should the frontend urls be localized
     * Locale is used as is because it is based on the Lang enum,
     * therefore we know its language tag will be either 'en' or 'fr'
     * @param uri the uri
     * @param locale the locale which will be used as uri prefix
     * @return the complete url
     */
    public String createUrl(String uri, Locale locale) {
        String realUri = messageSource.getMessage(uri, null, locale);
        return frontendUrl + "/" + locale.toLanguageTag() + "/" + realUri;
    }

    /**
     *
     * Creates a URL based on the uri, the locale provided, and some args
     * Locale is used as is because it is based on the Lang enum,
     * therefore we know its language tag will be either 'en' or 'fr'
     *
     * @param uri the uri as a th
     * @param locale the locale which will be used as a uri prefix
     * @return the complete url
     */
    public String createUrl(String uri, Locale locale, Object... args) {
        String realUri = messageSource.getMessage(uri, args, locale);
        return frontendUrl + "/" + locale.toLanguageTag() + "/" + realUri;
    }

    /**
     * Shortcut to generate /lang/me urls
     * @param locale the locale to use to create the url
     * @return the url to the user account
     */
    public String createMeUrl(Locale locale) {
        return createUrl("uri.me", locale);
    }

    private MimeMessage createMimeMessage(CustomMailMessage messageToSend, Locale locale, String htmlContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message =  mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(from, fromName));
        message.setRecipients(Message.RecipientType.TO, new InternetAddress(messageToSend.getRecipientMail(), messageToSend.getRecipientName()).toUnicodeString());
        message.setSubject(messageSource.getMessage(messageToSend.getSubject(), null, locale), "UTF-8");

        // send a multipart message to allow logo embedding
        Multipart multipart = new MimeMultipart("related");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
        multipart.addBodyPart(htmlPart);

        try {
            MimeBodyPart imgPart = new MimeBodyPart();
            imgPart.attachFile(logoTempFile);
            imgPart.setContentID("<logoImage>");
            multipart.addBodyPart(imgPart);
        }
        catch (IOException e) {
            log.error("Unable to load logo_email.png", e);
        }

        message.setContent(multipart);
        return message;
    }

    // TODO : improve exception handling with custom exceptions
    @Async
    public void send(CustomMailMessage messageToSend) {
        try {
            log.debug("MailProvider : begin sending message");
            Context context = new Context(messageToSend.getLocale());
            messageToSend.getVariables().forEach(context::setVariable);
            String htmlContent = templateEngine.process(messageToSend.getTemplate(), context);
            log.debug("Creating Mime Message to send");
            MimeMessage mimeMessage = createMimeMessage(messageToSend, messageToSend.getLocale(), htmlContent);
            log.debug("Passing the mimeMessage to the mail sender");
            mailSender.send(mimeMessage);
            log.debug("Mail should have been sent");
        }
        catch (Exception e) {
            if(e instanceof MessagingException || e instanceof MailException) {
                log.error("Unable to send message", e);
                throw new MailSendException("Unable to send message", e);
            }
            log.error("Unexpected exception while building or sending the message", e);
            throw new MailSendException("Unexpected exception while building or sending the message", e);
        }
    }
}