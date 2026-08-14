package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.infrastructure.security.captcha.AltchaCaptchaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.altcha.altcha.v2.Altcha;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wilhelm Zwertvaegher
 * TODO : add rate limiting
 */
@RestController
@Slf4j
@RequestMapping("/api/altcha")
@RequiredArgsConstructor
public class AltchaController {

    private final AltchaCaptchaValidator altchaCaptchaValidator;

    @GetMapping("/challenge")
    public Altcha.Challenge createChallenge() {
        try {
            return altchaCaptchaValidator.createChallenge();
        }
        catch (Exception e) {
            log.error("Unable to create altcha challenge {}", e.getMessage());
            throw new RuntimeException();
        }
    }
}