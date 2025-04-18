package com.wilzwert.myjobs.core.domain.ports.driven;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/04/2025
 * Time:17:12
 */

public class HtmlSanitizerTest {

    HtmlSanitizer sanitizer = html -> html.replaceAll("<script>", "").replaceAll("</script>", "");

    @Test
    void getAllowedTags_shouldReturnExpectedTags() {
        List<String> expectedTags = List.of("a", "p", "b", "i", "u", "div", "strong");
        assertIterableEquals(expectedTags, sanitizer.getAllowedTags());
    }

    @Test
    void sanitize_generic_shouldReturnSameInstance() {
        String input = "test";
        String sanitized = sanitizer.sanitize(input);
        assertEquals(input, sanitized);
    }

    @Test
    void sanitize_string_shouldRemoveScriptTags() {
        String input = "<p>Hello</p><script>alert('XSS');</script>";
        String output = sanitizer.sanitize(input);
        assertFalse(output.contains("<script>"));
    }

}
