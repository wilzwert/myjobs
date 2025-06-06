import { UserService } from './user.service';
import { DataService } from './data.service';
import { CaptchaService } from './captcha.service';
import { of, throwError } from 'rxjs';
import { ResetPasswordRequest } from '@core/model/reset-password-request.interface';
import { NewPasswordRequest } from '@core/model/new-password-request.interface';
import { ChangePasswordRequest } from '@core/model/change-password-request.interface';
import { ValidateEmailRequest } from '@core/model/validate-email-request.interface';
import { EditUserRequest } from '@core/model/edit-user-request.interface';
import { User } from '@core/model/user.interface';
import { UserSummary } from '../model/user-summary.interface';
import { JobStatus } from '../model/job.interface';
import { ApiError } from '../errors/api-error';
import { HttpErrorResponse } from '@angular/common/http';

describe('UserService', () => {
  let service: UserService;
  let dataServiceMock: jest.Mocked<DataService>;
  let captchaServiceMock: jest.Mocked<CaptchaService>;

  const fakeSummary: UserSummary = {
    jobsCount: 5,
    activeJobsCount: 3,
    inactiveJobsCount: 2,
    lateJobsCount: 1,
    jobStatuses: { [JobStatus.PENDING]: 3, [JobStatus.APPLICANT_REFUSED]: 2 },
    usableJobStatusMetas: []
  };

  beforeEach(() => {
    dataServiceMock = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      patch: jest.fn(),
      delete: jest.fn()
    } as unknown as jest.Mocked<DataService>;

    captchaServiceMock = {
      getCaptchaToken: jest.fn()
    } as unknown as jest.Mocked<CaptchaService>;

    service = new UserService(dataServiceMock, captchaServiceMock);
  });

  it('should reset password with captcha token', done => {
    const request: ResetPasswordRequest = { email: 'test@example.com' };
    captchaServiceMock.getCaptchaToken.mockReturnValue(of('token'));
    dataServiceMock.post.mockReturnValue(of(undefined));

    service.resetPassword(request).subscribe(() => {
      expect(captchaServiceMock.getCaptchaToken).toHaveBeenCalled();
      expect(dataServiceMock.post).toHaveBeenCalledWith('user/password/reset', request);
      done();
    });
  });

  it('should send new password with captcha token', done => {
    const request: NewPasswordRequest = { token: 'abc', password: '123456' };
    captchaServiceMock.getCaptchaToken.mockReturnValue(of('token'));
    dataServiceMock.post.mockReturnValue(of(undefined));

    service.newPassword(request).subscribe(() => {
      expect(captchaServiceMock.getCaptchaToken).toHaveBeenCalled();
      expect(dataServiceMock.post).toHaveBeenCalledWith('user/password', request);
      done();
    });
  });

  it('should change password directly via dataService', done => {
    const request: ChangePasswordRequest = { oldPassword: 'old', password: 'new' };
    dataServiceMock.put.mockReturnValue(of(undefined));

    service.changePassword(request).subscribe(() => {
      expect(dataServiceMock.put).toHaveBeenCalledWith('user/me/password', request);
      done();
    });
  });

  it('should send verification mail', done => {
    dataServiceMock.post.mockReturnValue(of(undefined));

    service.sendVerificationMail().subscribe(() => {
      expect(dataServiceMock.post).toHaveBeenCalledWith('user/me/email/verification', null);
      done();
    });
  });

  it('should validate email directly via dataService', done => {
    const request: ValidateEmailRequest = { code: 'abc' };
    dataServiceMock.post.mockReturnValue(of(undefined));

    service.validateEmail(request).subscribe(() => {
      expect(dataServiceMock.post).toHaveBeenCalledWith('user/me/email/validation', request);
      done();
    });
  });

  it('should get user', done => {
    const user: User = { id: '1', email: 'test@example.com', emailStatus: 'VERIFIED' } as unknown as User;
    dataServiceMock.get.mockReturnValue(of(user));

    service.getUser().subscribe(result => {
      expect(dataServiceMock.get).toHaveBeenCalledWith('user/me');
      expect(result).toEqual(user);
      done();
    });
  });

  it('should delete user', done => {
    dataServiceMock.delete.mockReturnValue(of(undefined));

    service.deleteUser().subscribe(() => {
      expect(dataServiceMock.delete).toHaveBeenCalledWith('user/me');
      done();
    });
  });

  it('should edit user', done => {
    const request: EditUserRequest = { username: 'new' } as EditUserRequest;
    const updatedUser: User = { id: '1', email: 'test@example.com', username: 'new' } as unknown as User;
    dataServiceMock.patch.mockReturnValue(of(updatedUser));

    service.editUser(request).subscribe(result => {
      expect(dataServiceMock.patch).toHaveBeenCalledWith('user/me', request);
      expect(result).toEqual(updatedUser);
      done();
    });
  });

  it('should save user language in uppercase', done => {
    dataServiceMock.put.mockReturnValue(of(undefined));

    service.saveUserLang('fr').subscribe(() => {
      expect(dataServiceMock.put).toHaveBeenCalledWith('user/me/lang', { lang: 'FR' });
      done();
    });
  });

  it('should call loadUserSummary and update userSummary signal', async () => {
    dataServiceMock.get.mockReturnValue(of(fakeSummary));

    await service.reloadUserSummary();

    const result = service.getUserSummary()();
    expect(result).toEqual(fakeSummary);
    expect(dataServiceMock.get).toHaveBeenCalledWith('user/me/summary');
  });

  it('should lazily load summary if not already loaded', () => {
    dataServiceMock.get.mockReturnValue(of(fakeSummary));

    // first access
    const signal = service.getUserSummary();
    expect(dataServiceMock.get).toHaveBeenCalledWith('user/me/summary');

    // simulate async update (we need to flush the observable)
    setTimeout(() => {
      expect(signal()).toEqual(fakeSummary);
    }, 0);
  });

  it('should not reload if already loaded', () => {
    dataServiceMock.get.mockReturnValue(of(fakeSummary));

    // call once to load it
    service.getUserSummary();

    // reset mock calls
    dataServiceMock.get.mockClear();

    // call again
    const signal = service.getUserSummary();
    expect(dataServiceMock.get).not.toHaveBeenCalled();
  });

  it('should set signal to false on loading error', () => {
    dataServiceMock.get.mockReturnValue(throwError(() => new ApiError({message: 'error'} as HttpErrorResponse) ));

    // first access
    const signal = service.getUserSummary();
    expect(dataServiceMock.get).toHaveBeenCalledWith('user/me/summary');

    // simulate async update (we need to flush the observable)
    setTimeout(() => {
      expect(signal()).toEqual(false);
    }, 0);
  });

});