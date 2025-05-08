import {
    AbstractControl,
    ValidationErrors,
    ValidatorFn,
  } from '@angular/forms';
  
  export const PasswordValidator: ValidatorFn = (
    control: AbstractControl
  ): ValidationErrors | null => {
    const value = control.value;

    if (!value) {
        return null;
    }

    let errors: string[] = [];

    const hasMinimumLength = value.length >= 8;
    if(!hasMinimumLength) {
        errors.push($localize `:@@error.password.too_short:too short (min 8 characters)`)
    }

    const hasMaximumLength = value.length <= 128;
    if(!hasMaximumLength) {
        errors.push($localize `:@@error.password.too_long:too long (max 128 characters)`)
    }

    const hasUpperCase = /[A-Z]+/.test(value);
    if(!hasUpperCase) {
        errors.push($localize `:@@error.password.uppercase:needs at least 1 uppercase character`);
    }

    const hasLowerCase = /[a-z]+/.test(value);
    if(!hasLowerCase) {
        errors.push($localize `:@@error.password.lowercase:needs at least 1 lowercase character`);
    }

    const hasNumeric = /[0-9]+/.test(value);
    if(!hasNumeric) {
        errors.push($localize `:@@error.password.digit:needs at least 1 digit`);
    }

    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(value);
    if(!hasSpecialChar) {
        errors.push($localize `:@@error.password.symbol:needs at least 1 symbol (!@#$%^&*(),.?":{}|<>)`);
    }

    const valid = hasMinimumLength && hasMaximumLength && hasUpperCase && hasLowerCase && hasNumeric && hasSpecialChar;
    const messages = !valid ? errors.join(', ') : '';
    return valid ? 
        null :
        {
            passwordStrength: {
                minimumLengthError: !hasMinimumLength,
                maximumLengthError: !hasMaximumLength,
                upperCaseError: !hasUpperCase,
                lowerCaseError: !hasLowerCase,
                numericError: !hasNumeric,
                specialCharError: !hasSpecialChar,
                message: messages.charAt(0).toUpperCase() + messages.slice(1)
            }
        }
  };
