package com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor.impl;


import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.jr.ob.JSON;
import com.wilzwert.myjobs.core.domain.model.JobMetadata;
import com.wilzwert.myjobs.core.domain.model.metadata.jsonld.JobPosting;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor.JobMetadataExtractor;


/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:54
 */
public class JsonLdJobMetadataExtractor implements JobMetadataExtractor {

    List<String> NOT_COMPATIBLE_DOMAINS = List.of("fhf.fr");

    Pattern JSON_LD_PATTERN = Pattern.compile(
            "<script\\s+type=[\"']application/ld\\+json[\"'][^>]*>((?:(?!</script>).)*?\"@type\"\\s*:\\s*\"JobPosting\"(?:(?!</script>).)*?)</script>",
            Pattern.DOTALL | Pattern.MULTILINE
    );

    private JobMetadata buildExtractedMetadataFromJobPosting(JobPosting jobPosting) {
        JobMetadata.Builder builder = new JobMetadata.Builder();
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
    public Optional<JobMetadata> extractJobMetadata(String html) {
        System.out.println("We are in the JsonLdJobMetadataExtractor");
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
        } catch (Exception e) {
            e.printStackTrace();
            // TODO : log
            return Optional.empty();
        }
    }

    @Override
    public boolean isCompatible(String domain) {
        System.out.println("We are in the JsonLdJobMetadataExtractor::isCompatible for "+domain);
        return !NOT_COMPATIBLE_DOMAINS.contains(domain);
    }
}
