package com.wilzwert.myjobs.core.domain.shared.exception;

import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationError;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationErrors;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
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


