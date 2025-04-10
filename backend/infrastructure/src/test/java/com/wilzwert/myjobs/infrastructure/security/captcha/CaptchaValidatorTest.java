package com.wilzwert.myjobs.infrastructure.security.captcha;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:10/04/2025
 * Time:08:51
 */
@ExtendWith(MockitoExtension.class)
public class CaptchaValidatorTest {

    @Mock
    private RestTemplate restTemplate;

    private CaptchaValidator underTest;

    @BeforeEach
    public void setUp() {
        underTest = new CaptchaValidator("secret", false, restTemplate);
    }

    @Test
    public void shouldReturnTrue_whenAlwaysTrue() {
        underTest = new CaptchaValidator("secret", true, restTemplate);

        assertTrue(underTest.validateCaptcha("response"));
        verify(restTemplate, times(0)).postForObject(anyString(), any(), eq(CaptchaResponse.class));
    }

    @Test
    public void shouldReturnTrue_whenCaptchaIsValid() {
        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        when(restTemplate.postForObject(anyString(), argument.capture(), eq(CaptchaResponse.class))).thenReturn(new CaptchaResponse().setSuccess(true));

        assertTrue(underTest.validateCaptcha("response"));
        verify(restTemplate, times(1)).postForObject(anyString(), argument.capture(), eq(CaptchaResponse.class));
        var value = argument.getValue();
        assertInstanceOf(LinkedMultiValueMap.class, value);
        @SuppressWarnings("unchecked")
        LinkedMultiValueMap<String, String> map = (LinkedMultiValueMap<String, String>) value;
        assertNotNull(map.get("secret"));
        assertNotNull(map.get("response"));
    }

    @Test
    public void shouldReturnFalse_whenCaptchaIsNotValid() {
        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        when(restTemplate.postForObject(anyString(), argument.capture(), eq(CaptchaResponse.class))).thenReturn(null);

        assertFalse(underTest.validateCaptcha("response"));

        verify(restTemplate, times(1)).postForObject(anyString(), argument.capture(), eq(CaptchaResponse.class));
        var value = argument.getValue();
        assertInstanceOf(LinkedMultiValueMap.class, value);
        @SuppressWarnings("unchecked")
        LinkedMultiValueMap<String, String> map = (LinkedMultiValueMap<String, String>) value;
        assertNotNull(map.get("secret"));
        assertNotNull(map.get("response"));
    }

}
