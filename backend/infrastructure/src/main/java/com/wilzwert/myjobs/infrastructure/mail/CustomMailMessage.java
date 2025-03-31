package com.wilzwert.myjobs.infrastructure.mail;

import java.util.HashMap;

public class CustomMailMessage {

    private String template;

    private String recipientMail;

    private String recipientName;

    private String subject;

    private String body;

    private HashMap<String, String> variables = new HashMap<>();

    public CustomMailMessage(String template, String recipientMail, String recipientName, String subject) {
        this.template = template;
        this.recipientMail = recipientMail;
        this.recipientName = recipientName;
        this.subject = subject;
        this.body = "";
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getRecipientMail() {
        return recipientMail;
    }

    public void setRecipientMail(String recipientMail) {
        this.recipientMail = recipientMail;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HashMap<String, String> getVariables() {
        return variables;
    }

    public void setVariables(HashMap<String, String> variables) {
        this.variables = variables;
    }

    public void setVariable(String key, String value) {
        this.variables.put(key, value);
    }


}
