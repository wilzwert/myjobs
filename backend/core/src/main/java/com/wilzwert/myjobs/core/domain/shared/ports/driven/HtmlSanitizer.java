package com.wilzwert.myjobs.core.domain.shared.ports.driven;


import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface HtmlSanitizer {
    default List<String> getAllowedTags() {
        return List.of("a", "p", "b", "i", "u", "div", "strong");
    }

    default <T> T sanitize(T source) {
        return source;
    }

    String sanitize(String html);
}
