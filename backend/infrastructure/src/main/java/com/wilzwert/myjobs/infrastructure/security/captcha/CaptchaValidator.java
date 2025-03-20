package com.wilzwert.myjobs.infrastructure.security.captcha;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class CaptchaValidator {

    private static final String GOOGLE_RECAPTCHA_ENDPOINT = "https://www.google.com/recaptcha/api/siteverify";

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${google.recaptcha.always-valid}")
    private boolean recaptchaAlwaysValid;


    public boolean validateCaptcha(String captchaResponse){
        if(recaptchaAlwaysValid) {
            return true;
        }

        log.info("Validating captcha");
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("secret", recaptchaSecret);
        requestMap.add("response", captchaResponse);

        CaptchaResponse apiResponse = restTemplate.postForObject(GOOGLE_RECAPTCHA_ENDPOINT, requestMap, CaptchaResponse.class);
        if(apiResponse == null){
            return false;
        }
        log.info("Captcha validation returned {}", apiResponse.isSuccess());
        return apiResponse.isSuccess();
    }
}