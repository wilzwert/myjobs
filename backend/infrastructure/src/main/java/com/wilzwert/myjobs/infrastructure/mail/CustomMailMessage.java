package com.wilzwert.myjobs.infrastructure.mail;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Locale;

@Setter
@Getter
public class CustomMailMessage {

    private String template;

    private String recipientMail;

    private String recipientName;

    private String subject;

    private String body;

    private String lang;

    private Locale locale;

    private HashMap<String, String> variables = new HashMap<>();

    public CustomMailMessage(String template, String recipientMail, String recipientName, String subject, String lang) {
        this.template = template;
        this.recipientMail = recipientMail;
        this.recipientName = recipientName;
        this.subject = subject;
        this.body = "";
        this.lang = lang;
        locale = Locale.forLanguageTag(lang.toLowerCase());
    }

    public void setVariable(String key, String value) {
        this.variables.put(key, value);
    }
}
