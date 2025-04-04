package com.wilzwert.myjobs.core.domain.service.metadata.extractor;


import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.jr.ob.JSON;
import com.wilzwert.myjobs.core.domain.service.metadata.extractor.jsonld.JobPosting;


/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:54
 */
public class JsonLdJobMetadataExtractor implements JobMetadataExtractor {
    Pattern JSON_LD_PATTERN = Pattern.compile(
            "<script\\s+type=[\"']application/ld\\+json[\"'][^>]*>((?:(?!</script>).)*?\"@type\"\\s*:\\s*\"JobPosting\"(?:(?!</script>).)*?)</script>",
            // "<script\\s+type=[\"']application/ld\\+json[\"'].*?>(.*?)</script>",
            Pattern.DOTALL | Pattern.MULTILINE
    );

    private ExtractedMetadata buildExtractedMetadataFromJobPosting(JobPosting jobPosting) {
        ExtractedMetadata.Builder builder = new ExtractedMetadata.Builder();
        try {
            builder.title(jobPosting.title());
            builder.description(jobPosting.description());
            builder.company(jobPosting.hiringOrganization().name());

            if(jobPosting.qualifications() != null) {
                builder.profile(jobPosting.qualifications());
            }
            else if(jobPosting.experienceRequirements() != null) {
                builder.profile(jobPosting.experienceRequirements());
            }
            builder.url(jobPosting.url());
            builder.salary(jobPosting.computeSalary());
        }
        catch (Exception e) {
            // TODO : log
            e.printStackTrace();
        }

        return builder.build();
    }


    @Override
    public Optional<ExtractedMetadata> getMetadata(String html) {
        if(html == null || html.isEmpty()) {
            return Optional.empty();
        }

        Matcher matcher = JSON_LD_PATTERN.matcher(html);
        try {
            while(matcher.find()) {
                if(matcher.group(1) != null) {
                    JobPosting jobPosting = JSON.std.beanFrom(JobPosting.class, matcher.group(1));
                    return Optional.of(buildExtractedMetadataFromJobPosting(jobPosting));
                }
            }
            return Optional.empty();

            // JsonNode jsonNode = mapper.readValue(matcher.group(1), JsonNode.class);
            // return buildExtractedMetadataFromJsonNode(jsonNode);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO : log
            return Optional.empty();
        }
    }

    @Override
    public boolean isIncompatible(String domain) {
        return false;
    }
}
