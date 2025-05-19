package com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.impl;

import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.JobMetadataExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:11
 */

public class HtmlJobMetadataExtractor implements JobMetadataExtractor {

    @Override
    public Optional<JobMetadata> extractJobMetadata(String html) {
        Document document = Jsoup.parse(html);
        String title = document.title();
        String description = document.select("meta[name=description]").attr("content");
        String h1 = document.select("h1").text();

        if(title.isEmpty() && description.isEmpty() && h1.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new JobMetadata(title.isEmpty() ? h1 : title, null, null, description, null, null));
    }

    @Override
    public boolean isCompatible(String domain) {
        return true;
    }
}
