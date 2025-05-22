package com.wilzwert.myjobs.infrastructure.adapter.fetcher;


import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.StaticHtmlFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:19
 */
@Component
@Slf4j
public class StaticHtmlFetcherAdapter implements StaticHtmlFetcher {
    // FIXME : this is not a very clean way of setting headers
    private static final Map<String, String> HEADERS = Map.of(
        "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:98.0) Gecko/20100101 Firefox/98.0",
        "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8",
        "Accept-Language", "en-US,en;q=0.5",
        "Accept-Encoding", "gzip, deflate",
        "Connection", "keep-alive",
        "Upgrade-Insecure-Requests", "1",
        "Sec-Fetch-Dest", "document",
        "Sec-Fetch-Mode", "navigate",
        "Sec-Fetch-Site", "none",
        "Sec-Fetch-User", "?1"
    );
    private final Map<String, String> OTHER_HEADERS = Map.of(
    "Cache-Control", "max-age=0"
    );

    @Override
    public Optional<String> fetchHtml(String url) {
        log.info("Fetching HTML from {}", url);

        try {
            Connection connection = Jsoup
                    .connect(url)
                    .ignoreContentType(true)
                    .referrer(url);

            HEADERS.forEach(connection::header);
            OTHER_HEADERS.forEach(connection::header);
            Document document = connection
                    .get();
            log.info("got html {}", document.html());
            return Optional.of(document.html());
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }
}
