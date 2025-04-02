package com.wilzwert.myjobs.infrastructure.adapter;


import com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher.StaticHtmlFetcher;
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
public class StaticHtmlFetcherAdapter implements StaticHtmlFetcher {

    private static Map<String, String> HEADERS = Map.of(
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
    private Map<String, String> OTHER_HEADERS = Map.of(
    "Cache-Control", "max-age=0"
    );

    @Override
    public Optional<String> fetchHtml(String url) {
        System.out.println("in StaticHtmlFetcherAdapter.fetchHtml");
        // Utilisation de Jsoup pour récupérer et parser le HTML de l'URL
        try {
            Connection connection = Jsoup
                    .connect(url)
                    .ignoreContentType(true)
                    .referrer(url);

            HEADERS.forEach((k, v) -> connection.header(k, v));
            OTHER_HEADERS.forEach((k, v) -> connection.header(k, v));

            Document document = connection
                    // .proxy("35.180.85.81", 20202)
                    // 35.180.85.81 20202
                    .get();
            return Optional.of(document.html());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }

        // return document.html(); // Retourne le code HTML complet
        /*System.out.println("in StaticHtmlFetcherAdapter.fetchHtml");
        return Optional.of("<html><head><title>Salut static</title></head><body><h1>Salut static</h1></body></html>");*/
    }
}
