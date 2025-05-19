import { ResetPasswordComponent } from './reset-password.component';
import { of, throwError } from 'rxjs';

describe('ResetPasswordComponent', () => {
  let component: ResetPasswordComponent;

  const userServiceMock = {
    resetPassword: jest.fn(),
  };

  const routerMock = {
    navigate: jest.fn(),
  };

  const notificationServiceMock = {
    confirmation: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();

    component = new ResetPasswordComponent(
      userServiceMock as any,
      routerMock as any,
      // FormBuilder ne peut pas être mocké ici, on peut utiliser l'original car Angular form est un service simple
      new (require('@angular/forms').FormBuilder)(),
      notificationServiceMock as any,
    );
  });

  it('should create form with email control', () => {
    expect(component.form.contains('email')).toBe(true);
  });

  it('should not submit if form is invalid', () => {
    component.form.controls['email'].setValue('not-an-email');
    component.submit();

    expect(userServiceMock.resetPassword).not.toHaveBeenCalled();
  });

  it('should submit and handle success', () => {
    component.form.controls['email'].setValue('test@example.com');
    userServiceMock.resetPassword.mockReturnValueOnce(of(undefined));

    component.submit();

    expect(userServiceMock.resetPassword).toHaveBeenCalledWith({ email: 'test@example.com' });
    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith(
      "Your request has been processed. If your email is linked to an account, an email has been sent. Please check your emails for further instructions."
    );
    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
    expect(component.isSubmitting).toBe(false);
  });

  it('should handle error on resetPassword failure', () => {
    component.form.controls['email'].setValue('test@example.com');
    userServiceMock.resetPassword.mockReturnValueOnce(
      throwError(() => new Error('Password request failed'))
    );

    component.submit();
    expect(component.isSubmitting).toBe(false);
    expect(notificationServiceMock.confirmation).not.toHaveBeenCalled();
    expect(routerMock.navigate).not.toHaveBeenCalled();
    expect(component.isSubmitting).toBe(false);
  });
});
