package com.wilzwert.myjobs.infrastructure.security.captcha;


import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;

import static com.wilzwert.myjobs.infrastructure.security.captcha.CaptchaAspect.CAPTCHA_HEADER_NAME;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:10/04/2025
 * Time:09:43
 */
@ExtendWith(MockitoExtension.class)
public class CaptchaAspectTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private CaptchaValidator captchaValidator;

    @InjectMocks
    private CaptchaAspect underTest;

    @Test
    public void shouldThrowAuthorizationDeniedException_whenCaptchaInvalid() {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(captchaValidator.validateCaptcha("response")).thenReturn(false);
        when(request.getHeader(CAPTCHA_HEADER_NAME)).thenReturn("response");

        assertThrows(AuthorizationDeniedException.class, () -> underTest.validateCaptcha(joinPoint));
    }

    @Test
    public void shouldProceed_whenCaptchaIsValid() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn(new Object());
        when(captchaValidator.validateCaptcha("response")).thenReturn(true);
        when(request.getHeader(CAPTCHA_HEADER_NAME)).thenReturn("response");

        assertDoesNotThrow(() -> underTest.validateCaptcha(joinPoint));

        verify(request, times(1)).getHeader(CAPTCHA_HEADER_NAME);
        verify(captchaValidator, times(1)).validateCaptcha("response");
        verify(joinPoint, times(1)).proceed();
    }

}

