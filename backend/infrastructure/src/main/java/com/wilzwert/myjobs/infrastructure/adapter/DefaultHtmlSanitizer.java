package com.wilzwert.myjobs.infrastructure.adapter;


import com.wilzwert.myjobs.core.domain.ports.driven.HtmlSanitizer;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;


/**
 * @author Wilhelm Zwertvaegher
 * Date:21/03/2025
 * Time:16:07
 */

@Component
public class DefaultHtmlSanitizer implements HtmlSanitizer {

    private final PolicyFactory policy;

    public DefaultHtmlSanitizer() {
        super();
        HtmlPolicyBuilder policyBuilder = new HtmlPolicyBuilder();

        getAllowedTags().forEach(policyBuilder::allowElements);

        if(getAllowedTags().contains("a")) {
            policyBuilder.allowAttributes("href").onElements("a")
                    .allowUrlProtocols("https")
                    .requireRelNofollowOnLinks();
        }

        policy = policyBuilder.toFactory();
    }

    @Override
    public String sanitize(String html) {
        System.out.println(html);
        String sanitizedHtml = policy.sanitize(html);
        System.out.println(sanitizedHtml);
        return sanitizedHtml;
    }
}
