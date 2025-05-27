import { LoginComponent } from './login.component';
import { FormBuilder } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { SessionService } from '../../core/services/session.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { SessionInformation } from '../../core/model/session-information.interface';
import { ApiError } from '../../core/errors/api-error';
import { ErrorProcessorService } from '../../core/services/error-processor.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let authServiceMock: jest.Mocked<AuthService>;
  let sessionServiceMock: jest.Mocked<SessionService>;
  let routerMock: jest.Mocked<Router>;
  let errorProcessorServiceMock: jest.Mocked<ErrorProcessorService>;
  let fb: FormBuilder;

  beforeEach(() => {
    authServiceMock = {
      login: jest.fn()
    } as unknown as jest.Mocked<AuthService>;

    sessionServiceMock = {
      logIn: jest.fn()
    } as unknown as jest.Mocked<SessionService>;

    routerMock = {
      navigate: jest.fn()
    } as unknown as jest.Mocked<Router>;

    fb = new FormBuilder();

    errorProcessorServiceMock = {
      processError: jest.fn()
    } as unknown as jest.Mocked<ErrorProcessorService>;

    component = new LoginComponent(authServiceMock, fb, sessionServiceMock, routerMock, errorProcessorServiceMock);
  });
  
  it('should initialize form with empty fields and not submitting', () => {
    expect(component.form).toBeDefined();
    expect(component.isSubmitting).toBe(false);
    expect(component.form.value).toEqual({ email: '', password: '' });
  });

  it('should submit login form successfully', () => {
    const mockResponse: SessionInformation = {
      username: 'user123',
      email: 'test@example.com',
      role: 'USER'
    };

    authServiceMock.login.mockReturnValue(of(mockResponse));

    component.form.setValue({
      email: 'test@example.com',
      password: 'pass123'
    });

    component.submit();

    expect(component.isSubmitting).toBe(false);
    expect(authServiceMock.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'pass123'
    });
    expect(sessionServiceMock.logIn).toHaveBeenCalledWith(mockResponse);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/jobs']);
  });
  
  it('should handle login 401 error and return localized message', () => {
    const apiError: ApiError = {
      httpStatus: 401,
    } as unknown as ApiError;

    authServiceMock.login.mockReturnValue(throwError(() => apiError));

    component.form.setValue({
      email: 'wrong@example.com',
      password: 'badpass'
    });

    component.submit();

    expect(component.isSubmitting).toBe(false);
    expect(authServiceMock.login).toHaveBeenCalled();
    expect(sessionServiceMock.logIn).not.toHaveBeenCalled();
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });
  
  it('should not call login if already submitting', () => {
    component.isSubmitting = true;

    component.form.setValue({
      email: 'x@y.com',
      password: '123'
    });

    component.submit();

    expect(authServiceMock.login).not.toHaveBeenCalled();
  });
});