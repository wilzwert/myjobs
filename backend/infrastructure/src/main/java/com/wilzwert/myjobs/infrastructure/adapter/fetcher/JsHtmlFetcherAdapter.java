package com.wilzwert.myjobs.infrastructure.adapter.fetcher;

import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.JsHtmlFetcher;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 */
@Component
public class JsHtmlFetcherAdapter implements JsHtmlFetcher {
    @Override
    public Optional<String> fetchHtml(String url) {
        return Optional.of("<html><head><title>JS</title></head><body>" + url + "</body></html>");
    }
}
