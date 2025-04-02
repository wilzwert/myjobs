package com.wilzwert.myjobs.infrastructure.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor.JsonLdJobMetadataExtractor;
import com.wilzwert.myjobs.core.domain.ports.driven.metadata.extractor.ExtractedMetadata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Wilhelm Zwertvaegher
 * Date:02/04/2025
 * Time:13:54
 */
public class JsonLdJobMetadataExtractorAdapter implements JsonLdJobMetadataExtractor {
    Pattern JSON_LD_PATTERN = Pattern.compile(
            "(?s)<script\\s+type=[\"']application/ld\\+json[\"'].*?>(.*?)</script>",
            Pattern.DOTALL
    );

    private String buildSalary(JsonNode baseSalaryNode) {
        String salaryType = baseSalaryNode.get("@type").asText();
        System.out.println(salaryType);
        return "oui";
    }

    private ExtractedMetadata buildExtractedMetadataFromJsonNode(JsonNode node) {
        ExtractedMetadata.Builder builder = new ExtractedMetadata.Builder();
        try {
            if (node.get("title") != null) {
                builder.title(node.get("title").asText());
            }
            if (node.get("hiringOrganization") != null && node.get("hiringOrganization").get("name") != null) {
                builder.company(node.get("hiringOrganization").get("name").asText());
            }
            if (node.get("description") != null) {
                builder.description(node.get("description").asText());
            }
            if(node.get("qualifications") != null) {
                builder.profile(node.get("qualifications").asText());
            }
            else if (node.get("experienceRequirements") != null) {
                builder.profile(node.get("experienceRequirements").asText());
            }
            if (node.get("baseSalary") != null) {
                builder.salary(buildSalary(node.get("baseSalary")));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return builder.build();
    }

    @Override
    public ExtractedMetadata getMetadata(String html) {
        Matcher matcher = JSON_LD_PATTERN.matcher(html);
        if (!matcher.find()) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            // parse as JsonNode for simplicity as some fields may come in various types
            while(matcher.find()) {
                if(matcher.group(1) != null) {
                    JsonNode jsonNode = mapper.readValue(matcher.group(1), JsonNode.class);
                    if(jsonNode.get("@type") != null && jsonNode.get("@type").asText().equals("JobPosting")) {
                        System.out.println(matcher.group(1));
                        return buildExtractedMetadataFromJsonNode(jsonNode);
                    }
                }
            }
            return null;

            // JsonNode jsonNode = mapper.readValue(matcher.group(1), JsonNode.class);
            // return buildExtractedMetadataFromJsonNode(jsonNode);
        } catch (JsonProcessingException e) {
            System.out.println("AH OK"+e.getMessage());
            return null;
        }
    }
}
