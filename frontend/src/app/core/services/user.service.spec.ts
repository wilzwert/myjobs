import { UserService } from './user.service';
import { DataService } from './data.service';
import { CaptchaService } from './captcha.service';
import { of } from 'rxjs';
import { ResetPasswordRequest } from '@core/model/reset-password-request.interface';
import { NewPasswordRequest } from '@core/model/new-password-request.interface';
import { ChangePasswordRequest } from '@core/model/change-password-request.interface';
import { ValidateEmailRequest } from '@core/model/validate-email-request.interface';
import { EditUserRequest } from '@core/model/edit-user-request.interface';
import { User } from '@core/model/user.interface';

describe('UserService', () => {
  let service: UserService;
  let dataService: jest.Mocked<DataService>;
  let captchaService: jest.Mocked<CaptchaService>;

  beforeEach(() => {
    dataService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      patch: jest.fn(),
      delete: jest.fn()
    } as unknown as jest.Mocked<DataService>;

    captchaService = {
      getCaptchaToken: jest.fn()
    } as unknown as jest.Mocked<CaptchaService>;

    service = new UserService(dataService, captchaService);
  });

  it('should reset password with captcha token', done => {
    const request: ResetPasswordRequest = { email: 'test@example.com' };
    captchaService.getCaptchaToken.mockReturnValue(of('token'));
    dataService.post.mockReturnValue(of(undefined));

    service.resetPassword(request).subscribe(() => {
      expect(captchaService.getCaptchaToken).toHaveBeenCalled();
      expect(dataService.post).toHaveBeenCalledWith('user/password/reset', request);
      done();
    });
  });

  it('should send new password with captcha token', done => {
    const request: NewPasswordRequest = { token: 'abc', password: '123456' };
    captchaService.getCaptchaToken.mockReturnValue(of('token'));
    dataService.post.mockReturnValue(of(undefined));

    service.newPassword(request).subscribe(() => {
      expect(captchaService.getCaptchaToken).toHaveBeenCalled();
      expect(dataService.post).toHaveBeenCalledWith('user/password', request);
      done();
    });
  });

  it('should change password directly via dataService', done => {
    const request: ChangePasswordRequest = { oldPassword: 'old', password: 'new' };
    dataService.put.mockReturnValue(of(undefined));

    service.changePassword(request).subscribe(() => {
      expect(dataService.put).toHaveBeenCalledWith('user/me/password', request);
      done();
    });
  });

  it('should send verification mail', done => {
    dataService.post.mockReturnValue(of(undefined));

    service.sendVerificationMail().subscribe(() => {
      expect(dataService.post).toHaveBeenCalledWith('user/me/email/verification', null);
      done();
    });
  });

  it('should validate email directly via dataService', done => {
    const request: ValidateEmailRequest = { code: 'abc' };
    dataService.post.mockReturnValue(of(undefined));

    service.validateEmail(request).subscribe(() => {
      expect(dataService.post).toHaveBeenCalledWith('user/me/email/validation', request);
      done();
    });
  });

  it('should get user', done => {
    const user: User = { id: '1', email: 'test@example.com', emailStatus: 'VERIFIED' } as unknown as User;
    dataService.get.mockReturnValue(of(user));

    service.getUser().subscribe(result => {
      expect(dataService.get).toHaveBeenCalledWith('user/me');
      expect(result).toEqual(user);
      done();
    });
  });

  it('should delete user', done => {
    dataService.delete.mockReturnValue(of(undefined));

    service.deleteUser().subscribe(() => {
      expect(dataService.delete).toHaveBeenCalledWith('user/me');
      done();
    });
  });

  it('should edit user', done => {
    const request: EditUserRequest = { username: 'new' } as EditUserRequest;
    const updatedUser: User = { id: '1', email: 'test@example.com', username: 'new' } as unknown as User;
    dataService.patch.mockReturnValue(of(updatedUser));

    service.editUser(request).subscribe(result => {
      expect(dataService.patch).toHaveBeenCalledWith('user/me', request);
      expect(result).toEqual(updatedUser);
      done();
    });
  });

  it('should save user language in uppercase', done => {
    dataService.put.mockReturnValue(of(undefined));

    service.saveUserLang('fr').subscribe(() => {
      expect(dataService.put).toHaveBeenCalledWith('user/me/lang', { lang: 'FR' });
      done();
    });
  });
});