package com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.impl;


import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.jr.ob.JSON;
import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;
import com.wilzwert.myjobs.core.domain.model.job.jsonld.JobPosting;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.JobMetadataExtractor;


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
        builder.title(jobPosting.title());
        builder.description(jobPosting.description());
        var organization = jobPosting.hiringOrganization();
        if(organization != null) {
            builder.company(organization.name());
        }

        if(jobPosting.qualifications() != null) {
            builder.profile(jobPosting.qualifications());
        }
        else if(jobPosting.experienceRequirements() != null) {
            builder.profile(jobPosting.experienceRequirements());
        }
        builder.url(jobPosting.url());
        builder.salary(jobPosting.computeSalary());
        return builder.build();
    }


    @Override
    public Optional<JobMetadata> extractJobMetadata(String html) {
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
            /* e.printStackTrace();
            System.out.println(e.getMessage());*/
            return Optional.empty();
        }
    }

    @Override
    public boolean isCompatible(String domain) {
        return NOT_COMPATIBLE_DOMAINS.stream().noneMatch(domain::matches);
    }
}