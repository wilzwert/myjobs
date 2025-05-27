import { TestBed, tick } from '@angular/core/testing';
import { AuthValidators } from './auth.validators';
import { AuthService } from '@core/services/auth.service';
import { of, throwError} from 'rxjs';
import { AbstractControl, ValidationErrors } from '@angular/forms';
import { DataService } from './data.service';
import { CaptchaService } from './captcha.service';

// TODO : test behaviour when captcha validation fails
describe('AuthValidators', () => {
  let authValidators: AuthValidators;
  let captchaServiceMock: jest.Mocked<CaptchaService> = {
    getCaptchaToken: jest.fn().mockReturnValue(of('captcha-token'))
  } as unknown as jest.Mocked<CaptchaService>;

  let dataServiceMock: jest.Mocked<DataService> = {
      post: jest.fn(),
      get: jest.fn()
  } as unknown as jest.Mocked<DataService>;


  // check common calls to captcha and data services
  function expectCommonCalls (
    type: String = 'email'
  ) {
    const [[url, options]] = dataServiceMock.get.mock.calls;
    const expectedUrl = 'auth/' + (type === 'email' ? 'email-check?email=new@example.com' : 'username-check?username=newusername');
    expect(url).toBe(expectedUrl);
    expect(options).toBeDefined();
    expect(options!.headers).toBeDefined();
    const headers = options!.headers;
    expect(headers!.get('Captcha-Response')).toEqual('captcha-token');
  }


  beforeEach(() => {
    jest.clearAllMocks();
    authValidators = new AuthValidators(dataServiceMock, captchaServiceMock);
  });

  it('should return null if email is unchanged (originalValue === control.value)', (done) => {
    const control: AbstractControl = { value: 'old@example.com' } as AbstractControl;
    const validatorFn = authValidators.checkEmailExistsAsync('old@example.com');

    validatorFn(control).subscribe(r => {
        expect(r).toBeNull();
        expect(captchaServiceMock.getCaptchaToken).not.toHaveBeenCalled();
        expect(dataServiceMock.get).not.toHaveBeenCalled();
        done();
    });
  });

  it('should return null if email is available', (done) => {
    dataServiceMock.get.mockReturnValue(of(true));
    const control: AbstractControl = { value: 'new@example.com' } as AbstractControl;
    const validatorFn = authValidators.checkEmailExistsAsync('old@example.com');
    validatorFn(control).subscribe(r => {
      expect(r).toBe(null);
      expectCommonCalls();
      done();
    });
  });


  it('should return { emailExists: true } if email is taken', (done) => {
    dataServiceMock.get.mockReturnValue(throwError(() => new Error('Email already exists')));

    const control: AbstractControl = { value: 'new@example.com' } as AbstractControl;
    const validatorFn = authValidators.checkEmailExistsAsync('old@example.com');
    validatorFn(control).subscribe(r => {
      expect(r).toEqual({emailExists:  true});
      expectCommonCalls();
      done();
    });
  });


  it('should return null if username is unchanged (originalValue === control.value)', (done) => {
    const control: AbstractControl = { value: 'oldusername' } as AbstractControl;
    const validatorFn = authValidators.checkUsernameExistsAsync('oldusername');

    validatorFn(control).subscribe(r => {
        expect(r).toBeNull();
        expect(captchaServiceMock.getCaptchaToken).not.toHaveBeenCalled();
        expect(dataServiceMock.get).not.toHaveBeenCalled();
        done();
    });
  });

  it('should return null if username is available', (done) => {
    dataServiceMock.get.mockReturnValue(of(true));
    const control: AbstractControl = { value: 'newusername' } as AbstractControl;
    const validatorFn = authValidators.checkUsernameExistsAsync('oldusername');
    validatorFn(control).subscribe(r => {
      expect(r).toBe(null);
      expectCommonCalls('username');
      done();
    });
  });


  it('should return { usernameExists: true } if email is taken', (done) => {
    dataServiceMock.get.mockReturnValue(throwError(() => new Error('Username already exists')));

    const control: AbstractControl = { value: 'newusername' } as AbstractControl;
    const validatorFn = authValidators.checkUsernameExistsAsync('oldusername');
    validatorFn(control).subscribe(r => {
      expect(r).toEqual({usernameExists:  true});
      expectCommonCalls('username');
      done();
    });
  });
});
