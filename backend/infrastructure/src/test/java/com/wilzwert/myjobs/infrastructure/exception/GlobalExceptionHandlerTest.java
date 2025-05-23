package com.wilzwert.myjobs.infrastructure.exception;


import com.wilzwert.myjobs.core.domain.model.user.exception.LoginException;
import com.wilzwert.myjobs.core.domain.shared.exception.DomainException;
import com.wilzwert.myjobs.core.domain.shared.exception.EntityAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.shared.exception.EntityNotFoundException;
import com.wilzwert.myjobs.core.domain.shared.exception.ValidationException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationErrors;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ErrorResponse;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author Wilhelm Zwertvaegher
 * Date:21/05/2025
 * Time:23:04
 */

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    public void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleEntityAlreadyExistsException() {
        var ex = new EntityAlreadyExistsException(ErrorCode.UNEXPECTED_ERROR) {};
        ResponseEntity<ErrorResponse> response = handler.generateError(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleEntityNotFoundException() {
        var ex = new EntityNotFoundException(ErrorCode.UNEXPECTED_ERROR) {};
        ResponseEntity<ErrorResponse> response = handler.generateError(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleValidationException() {
        var ex = new ValidationException(new ValidationErrors());
        ResponseEntity<ErrorResponse> response = handler.generateError(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleDomainException() {
        var ex = new DomainException(ErrorCode.UNEXPECTED_ERROR);
        ResponseEntity<ErrorResponse> response = handler.generateError(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleLoginException() {
        var ex = new LoginException();
        ResponseEntity<ErrorResponse> response = handler.generateError(ex);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleResponseStatusException() {
        var ex = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        ResponseEntity<ErrorResponse> response = handler.generateResponseStatusException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        MethodParameter parameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);
        var ex = new MethodArgumentNotValidException(parameter, bindingResult);
        ResponseEntity<ErrorResponse> response = handler.generateMethodArgumentNotValidException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleAccessDeniedException() {
        var ex = new AccessDeniedException("");
        ResponseEntity<ErrorResponse> response = handler.generateAccessDeniedException(ex);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleHttpRequestMethodNotSupportedException() {
        var ex = new HttpRequestMethodNotSupportedException("get");
        ResponseEntity<ErrorResponse> response = handler.generateHttpRequestMethodNotSupportedException(ex);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleHttpMediaTypeException() {
        var ex = new HttpMediaTypeNotAcceptableException("Error");
        ResponseEntity<ErrorResponse> response = handler.generateHttpMediaTypeException(ex);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleNumberFormatException() {
        var ex = new NumberFormatException("Error");
        ResponseEntity<ErrorResponse> response = handler.generateNumberFormatException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleBadRequestException() {
        var ex = new BadRequestException("Error");
        ResponseEntity<ErrorResponse> response = handler.generateBadRequestException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleHttpMessageNotReadableException() {
        var httpInputMessage = mock(HttpInputMessage.class);
        var ex = new HttpMessageNotReadableException("error", httpInputMessage);
        ResponseEntity<ErrorResponse> response = handler.generateBadRequestException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleHttpClientErrorException() {
        var ex = new HttpClientErrorException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        ResponseEntity<ErrorResponse> response = handler.generateHttpClientErrorException(ex);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleMissingServletRequestParameterException() {
        var ex = new MissingServletRequestParameterException("param", "string");
        ResponseEntity<ErrorResponse> response = handler.generateHttpClientErrorException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        var ex = new IllegalArgumentException("illegal argument");
        ResponseEntity<ErrorResponse> response = handler.generateIllegalArgumentException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleGenericException() {
        var ex = new Exception("generic error");
        ResponseEntity<ErrorResponse> response = handler.generateInternalErrorException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
