package com.wilzwert.myjobs.infrastructure.security.captcha;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CaptchaValidator {

    private static final String GOOGLE_RECAPTCHA_ENTERPRISE_ENDPOINT = "https://recaptchaenterprise.googleapis.com/v1/projects/myjobs-454309/assessments?key=";

    private final String recaptchaSiteKey;

    private final String recaptchaApiKey;

    private final boolean recaptchaAlwaysValid;

    private final RestTemplate restTemplate;

    public CaptchaValidator(
            @Value("${google.recaptcha.site-key}") String recaptchaSiteKey,
            @Value("${google.recaptcha.api-key}") String recaptchaApiKey,
            @Value("${google.recaptcha.always-valid}") boolean recaptchaAlwaysValid,
            RestTemplate restTemplate
    ) {
        this.recaptchaSiteKey = recaptchaSiteKey;
        this.recaptchaApiKey = recaptchaApiKey;
        this.recaptchaAlwaysValid = recaptchaAlwaysValid;
        this.restTemplate = restTemplate;
    }


    public boolean validateCaptcha(String captchaResponse){
        if(recaptchaAlwaysValid) {
            log.info("Recaptcha Always Valid");
            return true;
        }

        log.info("Validating captcha");

        Map<String, Object> event = new HashMap<>();
        event.put("token", captchaResponse);
        event.put("siteKey", recaptchaSiteKey);

        Map<String, Map<String, Object>> requestBody = new HashMap<>();
        requestBody.put("event", event);

        ResponseEntity<AssessmentResponse> apiResponse = restTemplate.postForEntity(GOOGLE_RECAPTCHA_ENTERPRISE_ENDPOINT +recaptchaApiKey, requestBody, AssessmentResponse.class);
        AssessmentResponse response = apiResponse.getBody();
        if(response == null) {
            return false;
        }

        log.info("Captcha validation status returned {}", response.getTokenProperties().isValid());
        return response.getTokenProperties().isValid();
    }
}