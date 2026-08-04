package com.wilzwert.myjobs.infrastructure.security.captcha;

public interface CaptchaValidator {

    boolean validateCaptcha(String captchaResponse);
}