package com.wilzwert.myjobs.infrastructure.adapter;


import com.wilzwert.myjobs.core.domain.shared.ports.driven.HtmlSanitizer;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;


/**
 * @author Wilhelm Zwertvaegher
 */

@Component
public class DefaultHtmlSanitizer implements HtmlSanitizer {

    private final PolicyFactory policy;

    public DefaultHtmlSanitizer() {
        super();
        HtmlPolicyBuilder policyBuilder = new HtmlPolicyBuilder();

        getAllowedTags().forEach(policyBuilder::allowElements);

        policyBuilder.allowUrlProtocols("https", "http");

        if(getAllowedTags().contains("a")) {
            policyBuilder.allowAttributes("href").onElements("a")
                    .requireRelNofollowOnLinks();
        }

        policy = policyBuilder.toFactory();
    }

    @Override
    public String sanitize(String html) {
        return policy.sanitize(html);
    }
}
