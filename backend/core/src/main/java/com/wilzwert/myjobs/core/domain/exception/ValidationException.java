package com.wilzwert.myjobs.core.domain.exception;

import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationError;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationErrors;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:25/07/2025
 * Time:12:10
 */

public class ValidationException extends DomainException {
  private final ValidationErrors errors;

  public ValidationException(ValidationErrors errors) {
    super(ErrorCode.VALIDATION_FAILED);
    this.errors = errors;
  }

  public ValidationErrors getErrors() {
    return errors;
  }

  public List<ValidationError> getFlatErrors() {
    return errors.getErrors().values().stream()
            .flatMap(List::stream)
            .toList();
  }
}


