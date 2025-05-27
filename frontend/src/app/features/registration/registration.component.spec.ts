import { RegistrationComponent } from './registration.component';
import { AuthService } from '@core/services/auth.service';
import { AuthValidators } from '@core/services/auth.validators';
import { NotificationService } from '@core/services/notification.service';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RegistrationRequest } from '@core/model/registration-request.interface';
import { ErrorProcessorService } from '@core/services/error-processor.service';

describe('RegistrationComponent', () => {
  let component: RegistrationComponent;  
  

  let authServiceMock: jest.Mocked<AuthService>;
  let authValidatorsMock: jest.Mocked<AuthValidators>;
  let notificationServiceMock: jest.Mocked<NotificationService>;
  let errorProcessorServiceMock: jest.Mocked<ErrorProcessorService>;
  let routerMock: jest.Mocked<Router>;

  beforeEach(async () => {
    authServiceMock = {
      register: jest.fn()
    } as unknown as jest.Mocked<AuthService>;

    authValidatorsMock = {
      checkEmailExistsAsync: jest.fn().mockReturnValue(() => of(null)),
      checkUsernameExistsAsync: jest.fn().mockReturnValue(() => of(null))
    } as unknown as jest.Mocked<AuthValidators>;

    notificationServiceMock = {
      confirmation: jest.fn()
    } as unknown as jest.Mocked<NotificationService>;

    routerMock = {
      navigate: jest.fn()
    } as unknown as jest.Mocked<Router>;

    errorProcessorServiceMock = {
        processError: jest.fn()
      } as unknown as jest.Mocked<ErrorProcessorService>;

    component = new RegistrationComponent(authServiceMock, authValidatorsMock, routerMock, new FormBuilder(), notificationServiceMock, errorProcessorServiceMock);

  });

  it('should create the form with required controls', () => {
    expect(component.form.contains('email')).toBe(true);
    expect(component.form.contains('username')).toBe(true);
    expect(component.form.contains('firstName')).toBe(true);
    expect(component.form.contains('lastName')).toBe(true);
    expect(component.form.contains('password')).toBe(true);
  });

  it('should submit the form and navigate on success', () => {
    // Arrange
    const formValue: RegistrationRequest = {
      email: 'test@example.com',
      username: 'testuser',
      firstName: 'John',
      lastName: 'Doe',
      password: 'StrongPass123!'
    };

    authServiceMock.register.mockReturnValue(of(null));

    component.form.setValue(formValue);

    // Act
    component.submit();

    // Assert
    expect(component.isSubmitting).toBe(false);
    expect(authServiceMock.register).toHaveBeenCalledWith(formValue);
    expect(notificationServiceMock.confirmation).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should not submit if form is invalid', () => {
    component.form.patchValue({
      email: '',
      username: '',
      firstName: '',
      lastName: '',
      password: ''
    });

    component.submit();

    expect(authServiceMock.register).not.toHaveBeenCalled();
  });

  it('should handle API error correctly', () => {
    const formValue: RegistrationRequest = {
      email: 'test@example.com',
      username: 'testuser',
      firstName: 'John',
      lastName: 'Doe',
      password: 'StrongPass123!'
    };

    const apiError = { message: 'Email already exists' };
    authServiceMock.register.mockReturnValue(throwError(() => apiError));

    component.form.setValue(formValue);

    expect(() => component.submit()).not.toThrow();

    expect(authServiceMock.register).toHaveBeenCalledWith(formValue);
    expect(component.isSubmitting).toBe(false);
  });
});
