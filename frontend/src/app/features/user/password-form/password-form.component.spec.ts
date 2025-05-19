import { PasswordFormComponent } from './password-form.component';
import { FormBuilder } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { NotificationService } from '../../../core/services/notification.service';
import { ApiError } from '../../../core/errors/api-error';
import { throwError, of } from 'rxjs';

describe('PasswordFormComponent', () => {
  let component: PasswordFormComponent;
  let userServiceMock: jest.Mocked<UserService>;
  let notificationServiceMock: jest.Mocked<NotificationService>;

  beforeEach(() => {
    userServiceMock = {
      changePassword: jest.fn(),
    } as unknown as jest.Mocked<UserService>;

    notificationServiceMock = {
      confirmation: jest.fn(),
    } as unknown as jest.Mocked<NotificationService>;

    component = new PasswordFormComponent(new FormBuilder(), userServiceMock, notificationServiceMock);
    component.success = jest.fn();
    component.fail = jest.fn();
    component.ngOnInit();
  });

  it('should not submit if form is invalid', () => {
    component.form.patchValue({
      oldPassword: '',
      password: '',
      passwordConfirmation: ''
    });

    component.submit();

    expect(userServiceMock.changePassword).not.toHaveBeenCalled();
    expect(component.isSubmitting).toBe(false);
  });

  it('should call changePassword and handle success', () => {
    userServiceMock.changePassword.mockReturnValue(of(void 0));

    component.form.patchValue({
      oldPassword: 'oldpass',
      password: 'newStrongPassword123!',
      passwordConfirmation: 'newStrongPassword123!'
    });

    component.submit();

    expect(userServiceMock.changePassword).toHaveBeenCalledWith({
      oldPassword: 'oldpass',
      password: 'newStrongPassword123!'
    });
    expect(component.isSubmitting).toBe(false);
    expect(notificationServiceMock.confirmation).toHaveBeenCalled();
    expect(component.success).toHaveBeenCalled();
  });

  it('should handle error when changePassword fails', () => {
    const error = new ApiError({ message: 'Invalid password' } as any);
    userServiceMock.changePassword.mockReturnValueOnce(throwError(() => error));
    
    component.form.patchValue({
      oldPassword: 'oldpass',
      password: 'goodStrongPass1!',
      passwordConfirmation: 'goodStrongPass1!'
    });

    component.submit();

    expect(userServiceMock.changePassword).toHaveBeenCalledWith({ oldPassword: 'oldpass', password: 'goodStrongPass1!'});
    expect(component.isSubmitting).toBe(false);
    expect(notificationServiceMock.confirmation).not.toHaveBeenCalled();
    expect(component.fail).toHaveBeenCalled();
  });
});
