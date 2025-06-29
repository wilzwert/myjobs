package com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.impl;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.jr.ob.JSON;
import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;
import com.wilzwert.myjobs.core.domain.model.job.jsonld.JobPosting;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.extractor.JobMetadataExtractor;


/**
 * @author Wilhelm Zwertvaegher
 */
public class JsonLdJobMetadataExtractor implements JobMetadataExtractor {

    // list of keys of the json ld format that we can use as String only
    private static final List<String> jsonLdValuesOnlyUsableAsStrings = List.of(
            "experienceRequirements",
            "educationRequirements",
            "qualifications"
    );

    private static final List<String> NOT_COMPATIBLE_DOMAINS = List.of("fhf.fr");

    private static final Pattern JSON_LD_PATTERN = Pattern.compile(
            "<script\\s+type=[\"']application/ld\\+json[\"'][^>]*>((?:(?!</script>).)*?\"@type\"\\s*:\\s*\"JobPosting\"(?:(?!</script>).)*?)</script>",
            Pattern.DOTALL | Pattern.MULTILINE
    );

    private JobMetadata buildExtractedMetadataFromJobPosting(JobPosting jobPosting) {
        JobMetadata.Builder builder = new JobMetadata.Builder();
        builder.title(jobPosting.title());
        builder.description(jobPosting.description());
        var organization = jobPosting.hiringOrganization();
        if(organization != null) {
            builder.company(organization.getName());
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

    private void removeIncompatibleMapData(Map<String, Object> data) {
        for(String key : jsonLdValuesOnlyUsableAsStrings) {
            Object value = data.get(key);
            if(value != null && !(value instanceof String)) {
                data.remove(key);
            }
        }
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
                    // first we get a map of values so that we can filter incompatible values
                    // before parsing into a JobPosting instance
                    Map<String, Object> data = JSON.std.mapFrom(matcher.group(1));
                    removeIncompatibleMapData(data);

                    JobPosting jobPosting = JSON.std
                            .without(JSON.Feature.FAIL_ON_UNKNOWN_BEAN_PROPERTY)
                            .beanFrom(JobPosting.class, JSON.std.asString(data));

                    return Optional.of(buildExtractedMetadataFromJobPosting(jobPosting));
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isCompatible(String domain) {
        return NOT_COMPATIBLE_DOMAINS.stream().noneMatch(domain::matches);
    }
}