package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import com.wilzwert.myjobs.core.domain.model.user.exception.LoginException;
import com.wilzwert.myjobs.core.domain.model.user.exception.PasswordMatchException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.shared.exception.ValidationException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationError;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationErrors;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:10/04/2025
 * Time:10:07
 */

public class ErrorResponseTest {
    // class used to simulate invalid parameter passed to Test::thing
    private static class Param {
        @NotNull(message = "FIELD_CANNOT_BE_EMPTY")
        private final String id;

        public Param() {
            id = null;
        }
    }

    // class used to manually throw a MethodArgumentNotValidException
    private static class ParamTest {
        public String thing(@Valid Param param) {
            return "param "+param;
        }
    }

    private void assertTimestampCloseToNow(long timestamp) {
        long diff = Instant.now().toEpochMilli() - timestamp;
        assertTrue(Math.abs(diff) < 300,
                "Timestamp is not within " + 300 + "ms of now");
    }


    @Test
    public void whenException_thenShouldBuildErrorResponse() {
        ErrorResponse response = ErrorResponse.fromException(new Exception());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatusCode());
        assertEquals("500", response.getStatus());
        assertEquals(ErrorCode.UNEXPECTED_ERROR.name(), response.getMessage());
        assertTimestampCloseToNow(response.getTimestamp());
    }

    @Test
    public void whenEntityAlreadyExistsException_thenShouldBuildErrorResponse() {
        ErrorResponse response = ErrorResponse.fromException(new UserAlreadyExistsException());
        assertEquals(HttpStatus.CONFLICT, response.getHttpStatusCode());
        assertEquals("409", response.getStatus());
        assertEquals("USER_ALREADY_EXISTS", response.getMessage());
        assertTimestampCloseToNow(response.getTimestamp());
    }

    @Test
    public void whenEntityNotFoundException_thenShouldBuildErrorResponse_() {
        ErrorResponse response = ErrorResponse.fromException(new UserNotFoundException());
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatusCode());
        assertEquals("404", response.getStatus());
        assertEquals("USER_NOT_FOUND", response.getMessage());
        assertTimestampCloseToNow(response.getTimestamp());
    }

    @Test
    public void whenResponseStatusException_thenShouldBuildErrorResponse() {
        ErrorResponse response = ErrorResponse.fromException(new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "unsupported"));
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getHttpStatusCode());
        assertEquals("415", response.getStatus());
        assertEquals("unsupported", response.getMessage());
        assertTimestampCloseToNow(response.getTimestamp());
    }

    @Test
    public void whenMethodArgumentNotValidException_thenShouldBuildErrorResponse() {
        // trigger "fake" validation to check ErrorResponse
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Param p = new Param();
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(p, "param");
        SpringValidatorAdapter adapter = new SpringValidatorAdapter(validator);
        adapter.validate(p, result);
        assertTrue(result.hasErrors());

        MethodArgumentNotValidException e;
        try {

             e = new MethodArgumentNotValidException(
                new MethodParameter(
                        ParamTest.class.getDeclaredMethod("thing", Param.class), 0
                ), result
             );
            ErrorResponse response = ErrorResponse.fromException(e);
            assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatusCode());
            assertEquals("400", response.getStatus());
            assertEquals(ErrorCode.VALIDATION_FAILED.name(), response.getMessage());
            assertEquals(1, response.getErrors().size());
            assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY.name(), response.getErrors().get("id").getFirst().getCode());
            assertTimestampCloseToNow(response.getTimestamp());
        }
        catch (NoSuchMethodException ex) {
            fail("Unexpected NoSuchMethodException: " + ex.getMessage());
        }
    }

    @Test
    public void whenLoginException_thenShouldBuildErrorResponse() {
        ErrorResponse response = ErrorResponse.fromException(new LoginException());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatusCode());
        assertEquals("401", response.getStatus());
        assertEquals("Access denied", response.getMessage());
        assertTimestampCloseToNow(response.getTimestamp());
    }

    @Test
    public void whenAccessDeniedException_thenShouldBuildErrorResponse() {
        ErrorResponse response = ErrorResponse.fromException(new AccessDeniedException("some message"));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatusCode());
        assertEquals("401", response.getStatus());
        assertEquals("Access denied", response.getMessage());
        assertTimestampCloseToNow(response.getTimestamp());
    }

    @Test
    public void whenHttpRequestMethodNotSupportedException_thenShouldBuildErrorResponse() {
        ErrorResponse response = ErrorResponse.fromException(new HttpRequestMethodNotSupportedException("some message"));
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getHttpStatusCode());
        assertEquals("405", response.getStatus());
        assertEquals("Unsupported method", response.getMessage());
        assertTimestampCloseToNow(response.getTimestamp());
    }

    @Test
    public void whenNumberFormatException_thenShouldBuildErrorResponse() {
        ErrorResponse response = ErrorResponse.fromException(new NumberFormatException("some message"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatusCode());
        assertEquals("400", response.getStatus());
        assertEquals("some message", response.getMessage());
        assertTimestampCloseToNow(response.getTimestamp());
    }

    @Test
    public void whenBadRequestException_thenShouldBuildErrorResponse() {
        ErrorResponse response = ErrorResponse.fromException(new BadRequestException("some message"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatusCode());
        assertEquals("400", response.getStatus());
        assertEquals("some message", response.getMessage());
        assertTimestampCloseToNow(response.getTimestamp());
    }

    @Test
    public void whenHttpMessageNotReadableException_thenShouldBuildErrorResponse() {
        ErrorResponse response = ErrorResponse.fromException(new HttpMessageNotReadableException("cannot read", new MockHttpInputMessage("input message".getBytes(StandardCharsets.UTF_8))));
        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatusCode());
        assertEquals("400", response.getStatus());
        assertEquals("cannot read", response.getMessage());
        assertTimestampCloseToNow(response.getTimestamp());
    }

    @Test
    public void whenHttpMediaTypeException_thenShouldBuildErrorResponse() {
        ErrorResponse response = ErrorResponse.fromException(new HttpMediaTypeNotAcceptableException("unsupported media type"));
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getHttpStatusCode());
        assertEquals("415", response.getStatus());
        assertEquals("unsupported media type", response.getMessage());
        assertTimestampCloseToNow(response.getTimestamp());
    }

    @Test
    public void whenHttpClientErrorException_thenShouldBuildErrorResponse() {
        ErrorResponse response = ErrorResponse.fromException(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Client error"));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatusCode());
        assertEquals("401", response.getStatus());
        assertEquals("401 Client error", response.getMessage());
        assertTimestampCloseToNow(response.getTimestamp());
    }

    @Test
    public void whenPasswordMatchException_thenShouldBuildErrorResponse() {
        ErrorResponse response = ErrorResponse.fromException(new PasswordMatchException());
        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatusCode());
        assertEquals("400", response.getStatus());
        assertEquals("USER_PASSWORD_MATCH_FAILED", response.getMessage());
        assertTimestampCloseToNow(response.getTimestamp());
    }

    @Test
    public void whenValidationException_thenShouldBuildErrorResponse() {
        ValidationErrors errors = new ValidationErrors();
        errors.add(new ValidationError("param", ErrorCode.FIELD_CANNOT_BE_EMPTY));
        ErrorResponse response = ErrorResponse.fromException(new ValidationException(errors));
        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatusCode());
        assertEquals(ErrorCode.VALIDATION_FAILED.name(), response.getMessage());
        assertEquals(1, response.getErrors().size());
        assertEquals(1, response.getErrors().get("param").size());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY.name(), response.getErrors().get("param").getFirst().getCode());
        assertTimestampCloseToNow(response.getTimestamp());
    }
}
