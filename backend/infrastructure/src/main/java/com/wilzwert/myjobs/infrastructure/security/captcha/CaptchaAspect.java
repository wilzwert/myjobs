package com.wilzwert.myjobs.infrastructure.security.captcha;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CaptchaAspect {

    private final HttpServletRequest request;

    private final CaptchaValidator captchaValidator;

    public static final String CAPTCHA_HEADER_NAME = "Captcha-Response";

    public CaptchaAspect(HttpServletRequest request, CaptchaValidator captchaValidator) {
        this.request = request;
        this.captchaValidator = captchaValidator;
    }

    @Around("@annotation(RequiresCaptcha)")
    public Object validateCaptcha(ProceedingJoinPoint joinPoint) throws Throwable {
        String captchaResponse = request.getHeader(CAPTCHA_HEADER_NAME);
        boolean isValidCaptcha = captchaValidator.validateCaptcha(captchaResponse);
        if(!isValidCaptcha){
            throw new AuthorizationDeniedException("Invalid captcha");
        }
        return joinPoint.proceed();
    }
}