package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher.JsHtmlFetcher;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:20
 */
@Component
public class JsHtmlFetcherAdapter implements JsHtmlFetcher {
    @Override
    public Optional<String> fetchHtml(String url) {
        return Optional.of("<html><head><title>JS</title></head><body>" + url + "</body></html>");
    }
}
