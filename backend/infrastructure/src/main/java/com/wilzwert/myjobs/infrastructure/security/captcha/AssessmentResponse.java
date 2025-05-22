package com.wilzwert.myjobs.infrastructure.security.captcha;


import lombok.Data;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:20/05/2025
 * Time:21:51
 */
@Data
public class AssessmentResponse {
        @Data
        static class Event {
            private String expectedAction;
            private String hashedAccountId;
            private String siteKey;
            private String token;
            private String userAgent;
            private String userIpAddress;

            // Getters et setters
        }

        @Data
        static class RiskAnalysis {
            private List<String> reasons;
            private String score;

            // Getters et setters
        }
        @Data
        static class TokenProperties {
            private String action;
            private String createTime;
            private String hostname;
            private String invalidReason;
            private boolean valid;

            // Getters et setters
        }

        private Event event;
        private String name;
        private RiskAnalysis riskAnalysis;
        private TokenProperties tokenProperties;
    // Getters et setters
}