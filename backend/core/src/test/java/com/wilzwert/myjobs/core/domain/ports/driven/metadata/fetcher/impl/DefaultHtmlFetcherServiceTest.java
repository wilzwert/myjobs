package com.wilzwert.myjobs.core.domain.ports.driven.metadata.fetcher.impl;


import com.wilzwert.myjobs.core.domain.shared.exception.NoHtmlFetcherException;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.HtmlFetcher;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.fetcher.impl.DefaultHtmlFetcherService;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 */
class DefaultHtmlFetcherServiceTest {
    @Test
    void fetchWithNoFetcher_throwsNoHtmlFetcherException() {
        DefaultHtmlFetcherService underTest = new DefaultHtmlFetcherService();
        assertThrows(NoHtmlFetcherException.class, () -> underTest.fetchHtml("example.com", "some html"));
    }

    @Test
    void fetchHtmlWithCompatibleFetcher_returnsHtml() {
        HtmlFetcher fetcher = new HtmlFetcher() {
            @Override
            public Optional<String> fetchHtml(String url) {
                return Optional.of("<html>mock</html>");
            }

            @Override
            public boolean isCompatible(String url) {
                return true;
            }
        };
        DefaultHtmlFetcherService service = new DefaultHtmlFetcherService().with(fetcher);

        Optional<String> result = service.fetchHtml("domain.com", "http://url");

        assertTrue(result.isPresent());
        assertEquals("<html>mock</html>", result.get());
    }

    @Test
    void fetchHtmlWithInCompatibleFetcher_returnsEmptyResult() {
        HtmlFetcher fetcher = new HtmlFetcher() {
            @Override
            public Optional<String> fetchHtml(String url) {
                return Optional.of("<html>mock</html>");
            }

            @Override
            public boolean isCompatible(String url) {
                return false;
            }
        };
        DefaultHtmlFetcherService service = new DefaultHtmlFetcherService().with(fetcher);

        Optional<String> result = service.fetchHtml("domain.com", "http://url");

        assertTrue(result.isEmpty());
    }

}
