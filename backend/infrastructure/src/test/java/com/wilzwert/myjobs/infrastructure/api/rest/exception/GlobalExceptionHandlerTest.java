package com.wilzwert.myjobs.infrastructure.api.rest.exception;


import com.wilzwert.myjobs.core.domain.model.user.exception.LoginException;
import com.wilzwert.myjobs.core.domain.shared.exception.DomainException;
import com.wilzwert.myjobs.core.domain.shared.exception.EntityAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.shared.exception.EntityNotFoundException;
import com.wilzwert.myjobs.core.domain.shared.exception.ValidationException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationErrors;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    void shouldHandleGenericException() {
        var ex = new Exception("generic error");
        ResponseEntity<ErrorResponse> response = handler.generateInternalErrorException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
