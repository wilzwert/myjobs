import { AbstractControl } from '@angular/forms';
import { PasswordValidator } from './password-validator'; // Change le chemin selon ton projet

describe('PasswordValidator', () => {
  
  let control: AbstractControl;

  it('should return null for an empty value', () => {
    control = { value: null } as AbstractControl;
    
    const result = PasswordValidator(control);
    
    // no error expected
    expect(result).toBeNull();
  });

  it('should return null for a valid password', () => {
    control = { value: 'Valid1Password@123' } as AbstractControl;
    
    const result = PasswordValidator(control);
    
    // no error expected
    expect(result).toBeNull();
  });

  it('should return an error for a password that is too short', () => {
    control = { value: 'Short1!' } as AbstractControl;
    
    const result = PasswordValidator(control);
  
    expect(result).toEqual({
      passwordStrength: {
        minimumLengthError: true,
        maximumLengthError: false,
        upperCaseError: false,
        lowerCaseError: false,
        numericError: false,
        specialCharError: false,
        message: 'Too short (min 8 characters)'
      }
    });
  });

  it('should return an error for a password that is too long', () => {
    control = { value: 'A'.repeat(129) + 'a1!' } as AbstractControl;
    
    const result = PasswordValidator(control);
  
    expect(result).toEqual({
      passwordStrength: {
        minimumLengthError: false,  
        maximumLengthError: true,   
        upperCaseError: false,      
        lowerCaseError: false,      
        numericError: false,        
        specialCharError: false,    
        message: 'Too long (max 128 characters)'
      }
    });
  });

  it('should return an error for a password without an uppercase letter', () => {
    control = { value: 'password1!' } as AbstractControl;
    
    const result = PasswordValidator(control);
  
    expect(result).toEqual({
      passwordStrength: {
        minimumLengthError: false,  
        maximumLengthError: false,
        upperCaseError: true,
        lowerCaseError: false,
        numericError: false,
        specialCharError: false,
        message: 'Needs at least 1 uppercase character'
      }
    });
  });

  it('should return an error for a password without a lowercase letter', () => {
    control = { value: 'PASSWORD1!' } as AbstractControl;
    
    const result = PasswordValidator(control);
  
    expect(result).toEqual({
      passwordStrength: {
        minimumLengthError: false,  
        maximumLengthError: false,
        upperCaseError: false,
        lowerCaseError: true,
        numericError: false,
        specialCharError: false,
        message: 'Needs at least 1 lowercase character'
      }
    });
  });


  it('should return an error for a password without a special character', () => {
    control = { value: 'Password1' } as AbstractControl;
    
    const result = PasswordValidator(control);
  
    expect(result).toEqual({
      passwordStrength: {
        minimumLengthError: false,
        maximumLengthError: false,
        upperCaseError: false,
        lowerCaseError: false,
        numericError: false,
        specialCharError: true,
        message: 'Needs at least 1 symbol (!@#$%^&*(),.?":{}|<>)'
      }
    });
  });

  it('should return an error for a password that is missing multiple requirements', () => {
    control = { value: 'short' } as AbstractControl;
    
    const result = PasswordValidator(control);
  
    expect(result).toEqual({
      passwordStrength: {
        minimumLengthError: true,
        maximumLengthError: false,
        upperCaseError: true,
        lowerCaseError: false,
        numericError: true,
        specialCharError: true,
        message: 'Too short (min 8 characters), needs at least 1 uppercase character, needs at least 1 digit, needs at least 1 symbol (!@#$%^&*(),.?":{}|<>)'
      }
    });
  });
  
});
