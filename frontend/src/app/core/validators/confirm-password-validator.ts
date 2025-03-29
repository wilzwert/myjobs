import {
    AbstractControl,
    ValidationErrors,
    ValidatorFn,
  } from '@angular/forms';
  
  export const ConfirmPasswordValidator: ValidatorFn = (
    control: AbstractControl
  ): ValidationErrors | null => {
    console
    return control.value.password === control.value.passwordConfirmation
      ? null
      : { passwordNoMatch: true };
  };