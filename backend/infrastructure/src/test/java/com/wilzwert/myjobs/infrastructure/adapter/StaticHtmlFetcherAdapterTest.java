package com.wilzwert.myjobs.infrastructure.adapter;

import com.wilzwert.myjobs.infrastructure.adapter.fetcher.StaticHtmlFetcherAdapter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StaticHtmlFetcherAdapterTest {

    private StaticHtmlFetcherAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new StaticHtmlFetcherAdapter();
    }

    @Test
    void whenConnectionSucceeds_fetchHtml_shouldReturnHtml() throws IOException {
        String url = "https://example.com";
        String html = "<html><body>Hello World</body></html>";

        Connection mockConnection = mock(Connection.class);
        Document mockDocument = mock(Document.class);

        when(mockConnection.ignoreContentType(true)).thenReturn(mockConnection);
        when(mockConnection.referrer(url)).thenReturn(mockConnection);
        when(mockConnection.header(anyString(), anyString())).thenReturn(mockConnection);
        when(mockConnection.get()).thenReturn(mockDocument);
        when(mockDocument.html()).thenReturn(html);

        try (MockedStatic<Jsoup> jsoupMockedStatic = mockStatic(Jsoup.class)) {
            jsoupMockedStatic.when(() -> Jsoup.connect(url)).thenReturn(mockConnection);

            Optional<String> result = adapter.fetchHtml(url);

            assertThat(result).isPresent();
            assertThat(result.get()).contains(html);

            // Verify Jsoup.connect was called
            jsoupMockedStatic.verify(() -> Jsoup.connect(url), times(1));
        }
    }

    @Test
    void whenIOExceptionOccurs_fetchHtml_shouldReturnEmpty() throws IOException {
        String url = "https://example.com";

        Connection mockConnection = mock(Connection.class);

        when(mockConnection.ignoreContentType(true)).thenReturn(mockConnection);
        when(mockConnection.referrer(url)).thenReturn(mockConnection);
        when(mockConnection.header(anyString(), anyString())).thenReturn(mockConnection);
        when(mockConnection.get()).thenThrow(new IOException("Network error"));

        try (MockedStatic<Jsoup> jsoupMockedStatic = mockStatic(Jsoup.class)) {
            jsoupMockedStatic.when(() -> Jsoup.connect(url)).thenReturn(mockConnection);

            Optional<String> result = adapter.fetchHtml(url);

            assertThat(result).isEmpty();

            jsoupMockedStatic.verify(() -> Jsoup.connect(url), times(1));
        }
    }
}