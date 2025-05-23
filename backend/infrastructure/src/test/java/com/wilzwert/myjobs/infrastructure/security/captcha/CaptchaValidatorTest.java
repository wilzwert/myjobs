package com.wilzwert.myjobs.infrastructure.security.captcha;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    void setUp() {
        underTest = new CaptchaValidator("siteKey", "apiKey", false, restTemplate);
    }

    @Test
    void shouldReturnTrue_whenAlwaysTrue() {
        underTest = new CaptchaValidator( "siteKey", "apiKey", true, restTemplate);

        assertTrue(underTest.validateCaptcha("response"));
        verify(restTemplate, times(0)).postForObject(anyString(), any(), eq(AssessmentResponse.class));
    }

    @Test
    void shouldReturnTrue_whenCaptchaIsValid() {
        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        AssessmentResponse response = new AssessmentResponse();
        AssessmentResponse.TokenProperties properties = new AssessmentResponse.TokenProperties();
        properties.setValid(true);
        response.setTokenProperties(properties);
        when(restTemplate.postForEntity(anyString(), argument.capture(), eq(AssessmentResponse.class))).thenReturn(ResponseEntity.ok(response));

        assertTrue(underTest.validateCaptcha("captchaResponse"));
        verify(restTemplate, times(1)).postForEntity(anyString(), argument.capture(),  eq(AssessmentResponse.class));
        var value = argument.getValue();
        assertInstanceOf(HashMap.class, value);
        @SuppressWarnings("unchecked")
        HashMap<String, Map<String, Object>> map = (HashMap<String, Map<String, Object>>) value;
        assertNotNull(map.get("event"));
        assertThat(map.get("event").get("token")).isEqualTo("captchaResponse");
        assertThat(map.get("event").get("siteKey")).isEqualTo("siteKey");
    }

    @Test
    void shouldReturnFalse_whenCaptchaIsNotValid() {
        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        AssessmentResponse response = new AssessmentResponse();
        AssessmentResponse.TokenProperties properties = new AssessmentResponse.TokenProperties();
        properties.setValid(false);
        response.setTokenProperties(properties);
        when(restTemplate.postForEntity(anyString(), argument.capture(), eq(AssessmentResponse.class))).thenReturn(ResponseEntity.ok(response));

        assertFalse(underTest.validateCaptcha("captchaResponse"));
        verify(restTemplate, times(1)).postForEntity(anyString(), argument.capture(),  eq(AssessmentResponse.class));
        var value = argument.getValue();
        assertInstanceOf(HashMap.class, value);
        @SuppressWarnings("unchecked")
        HashMap<String, Map<String, Object>> map = (HashMap<String, Map<String, Object>>) value;
        assertNotNull(map.get("event"));
        assertThat(map.get("event").get("token")).isEqualTo("captchaResponse");
        assertThat(map.get("event").get("siteKey")).isEqualTo("siteKey");
    }

    @Test
    void shouldReturnFalse_whenEmptyResponseFromRecaptchaApi() {
        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        when(restTemplate.postForEntity(anyString(), argument.capture(), eq(AssessmentResponse.class))).thenReturn(ResponseEntity.ok(null));

        assertFalse(underTest.validateCaptcha("captchaResponse"));
        verify(restTemplate, times(1)).postForEntity(anyString(), argument.capture(),  eq(AssessmentResponse.class));
        var value = argument.getValue();
        assertInstanceOf(HashMap.class, value);
        @SuppressWarnings("unchecked")
        HashMap<String, Map<String, Object>> map = (HashMap<String, Map<String, Object>>) value;
        assertNotNull(map.get("event"));
        assertThat(map.get("event").get("token")).isEqualTo("captchaResponse");
        assertThat(map.get("event").get("siteKey")).isEqualTo("siteKey");
    }

    @Test
    void shouldReturnFalse_whenResponseFromRecaptchaApiNotOk() {
        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        when(restTemplate.postForEntity(anyString(), argument.capture(), eq(AssessmentResponse.class))).thenReturn(ResponseEntity.badRequest().build());

        assertFalse(underTest.validateCaptcha("captchaResponse"));
        verify(restTemplate, times(1)).postForEntity(anyString(), argument.capture(),  eq(AssessmentResponse.class));
        var value = argument.getValue();
        assertInstanceOf(HashMap.class, value);
        @SuppressWarnings("unchecked")
        HashMap<String, Map<String, Object>> map = (HashMap<String, Map<String, Object>>) value;
        assertNotNull(map.get("event"));
        assertThat(map.get("event").get("token")).isEqualTo("captchaResponse");
        assertThat(map.get("event").get("siteKey")).isEqualTo("siteKey");
    }
}
