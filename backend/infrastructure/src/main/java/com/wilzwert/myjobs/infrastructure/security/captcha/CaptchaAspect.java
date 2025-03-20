package com.wilzwert.myjobs.infrastructure.security.captcha;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class CaptchaAspect {
    private final CaptchaValidator captchaValidator;

    public CaptchaAspect(CaptchaValidator captchaValidator) {
        this.captchaValidator = captchaValidator;
    }

    private static final String CAPTCHA_HEADER_NAME = "Captcha-Response";

    @Around("@annotation(RequiresCaptcha)")
    public Object validateCaptcha(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String captchaResponse = request.getHeader(CAPTCHA_HEADER_NAME);
        boolean isValidCaptcha = captchaValidator.validateCaptcha(captchaResponse);
        if(!isValidCaptcha){
            throw new AuthorizationDeniedException("Invalid captcha");
        }
        return joinPoint.proceed();
    }
}