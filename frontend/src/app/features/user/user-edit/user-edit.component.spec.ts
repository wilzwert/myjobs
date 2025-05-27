import { UserEditComponent } from './user-edit.component';
import { FormBuilder } from '@angular/forms';
import { AuthValidators } from '@core/services/auth.validators';
import { UserService } from '@core/services/user.service';
import { NotificationService } from '@core/services/notification.service';
import { FormErrorService } from '@core/services/form-error.service';
import { of, throwError } from 'rxjs';
import { User } from '@core/model/user.interface';
import { ApiError } from '@core/errors/api-error';
import { EmailStatus } from '@core/model/email-status';

describe('UserEditComponent', () => {
  let component: UserEditComponent;

  const mockUser: User = {
    email: 'user@example.com',
    username: 'existinguser',
    firstName: 'John',
    lastName: 'Doe',
    createdAt: '',
    emailStatus: EmailStatus.VALIDATED
  };

  const mockUserService = {
    editUser: jest.fn()
  };

  const mockNotificationService = {
    confirmation: jest.fn()
  };

  const mockFormErrorService = {
    setBackendErrors: jest.fn(),
    cleanup: jest.fn()
  };

  const mockAuthValidators = {
    checkEmailExistsAsync: jest.fn(() => () => of(null)),
    checkUsernameExistsAsync: jest.fn(() => () => of(null))
  };

  beforeEach(() => {
    component = new UserEditComponent(
      mockAuthValidators as any,
      new FormBuilder(),
      mockUserService as any,
      mockNotificationService as any,
      mockFormErrorService as any
    );
    (component as any).data = { user: mockUser };
    component.ngOnInit();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should initialize the form with user values', () => {
    expect(component['form'].value).toEqual({
      email: mockUser.email,
      username: mockUser.username,
      firstName: mockUser.firstName,
      lastName: mockUser.lastName
    });
  });

  it('should call editUser and show notification on successful submit', () => {
    mockUserService.editUser.mockReturnValue(of({}));
    component.submit();
    expect(mockUserService.editUser).toHaveBeenCalledWith(component['form'].value);
    expect(mockNotificationService.confirmation).toHaveBeenCalledWith(expect.stringContaining('updated'));
  });

  it('should handle backend error and call setBackendErrors', () => {
    const apiError: ApiError = {
      message: 'Validation failed',
      errors: new Map<string, string[]>([
        ['email', ['Email already taken']]
      ])
    } as ApiError;

    mockUserService.editUser.mockReturnValue(throwError(() => apiError));
    component.submit();
    expect(mockFormErrorService.setBackendErrors).toHaveBeenCalledWith(component['form'], apiError.errors);
  });
});