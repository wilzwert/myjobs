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
        errors.push('too short, (min 8 characters)')
    }

    const hasMaximumLength = value.length <= 16;
    if(!hasMinimumLength) {
        errors.push('too long (max 16 characters)')
    }

    const hasUpperCase = /[A-Z]+/.test(value);
    if(!hasUpperCase) {
        errors.push('needs at least 1 uppercase character');
    }

    const hasLowerCase = /[a-z]+/.test(value);
    if(!hasLowerCase) {
        errors.push('needs at least 1 lowercase character');
    }

    const hasNumeric = /[0-9]+/.test(value);
    if(!hasNumeric) {
        errors.push('needs at least 1 digit');
    }

    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(value);
    if(!hasSpecialChar) {
        errors.push('needs at least 1 symbol (!@#$%^&*(),.?":{}|<>)');
    }

    const valid = hasMinimumLength && hasMaximumLength && hasUpperCase && hasLowerCase && hasNumeric && hasSpecialChar;
    const messages = !valid ? errors.join(', ') : '';
    return valid ? 
        null :
        {
            passwordStrength: {
                hasMinimumLength: !hasMinimumLength,
                hasMaximumLength: !hasMaximumLength,
                hasUpperCase: !hasUpperCase,
                hasLowerCase: !hasLowerCase,
                hasNumeric: !hasNumeric,
                hasSpecialChar: !hasSpecialChar,
                message: messages.charAt(0).toUpperCase() + messages.slice(1)
            }
        }
  };
