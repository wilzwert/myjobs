package com.wilzwert.myjobs.infrastructure.utility;


import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/05/2025
 * Time:13:23
 */

public class EmailUtility {

    public static String extractHtmlContent(MimeMessage message) throws Exception {
        Object content = message.getContent();

        if (content instanceof String) {
            return (String) content;
        }

        if (content instanceof Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (part.getContentType().toLowerCase().startsWith("text/html")) {
                    return (String) part.getContent();
                }
            }
        }

        throw new IllegalStateException("No HTML content found in the message");
    }
}
