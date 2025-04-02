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
            "<script\\s+type=[\"']application/ld\\+json[\"'].*?>(.*?)</script>",
            Pattern.DOTALL | Pattern.MULTILINE
    );

    private String buildSalary(JsonNode node) {
        JsonNode baseSalaryNode = node.get("baseSalary");
        if (baseSalaryNode == null) {
            return "";
        }
        String salaryType = baseSalaryNode.get("@type").asText();
        String result = "";
        try {
            result = switch (salaryType) {
                case "MonetaryAmount" ->
                        switch(baseSalaryNode.get("value").get("@type").asText()) {
                            case "QuantitativeValue" ->
                                    baseSalaryNode.get("value").get("minValue").asText()
                                    +" - "+baseSalaryNode.get("value").get("maxValue").asText();
                            default -> baseSalaryNode.get("value").get("value").asText();
                        }
                        + " "+baseSalaryNode.get("currency").asText()
                        + " / " + baseSalaryNode.get("value").get("unitText").asText();
                case "Number" -> baseSalaryNode.get("value").asText();
                default -> "";
            };
        }
        catch (Exception e) {
            // TODO : log
            e.printStackTrace();
        }
        System.out.println(salaryType);
        return result;
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
            builder.salary(buildSalary(node));
        }
        catch (Exception e) {
            // TODO : log
            e.printStackTrace();
        }

        return builder.build();
    }

    @Override
    public ExtractedMetadata getMetadata(String html) {
        Matcher matcher = JSON_LD_PATTERN.matcher(html);

        try {
            ObjectMapper mapper = new ObjectMapper();
            // parse as JsonNode for simplicity as some fields may come in various types
            while(matcher.find()) {
                if(matcher.group(1) != null) {
                    JsonNode jsonNode = mapper.readValue(matcher.group(1), JsonNode.class);
                    if(jsonNode.get("@type") != null && jsonNode.get("@type").asText().equals("JobPosting")) {
                        return buildExtractedMetadataFromJsonNode(jsonNode);
                    }
                }
            }
            return null;

            // JsonNode jsonNode = mapper.readValue(matcher.group(1), JsonNode.class);
            // return buildExtractedMetadataFromJsonNode(jsonNode);
        } catch (JsonProcessingException e) {
            // TODO : log
            return null;
        }
    }
}
