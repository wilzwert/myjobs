import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from './auth.service';
import { LoginRequest } from '../model/login-request.interface';
import { RegistrationRequest } from '../model/registration-request.interface';
import { SessionInformation } from '../model/session-information.interface';
import { of, throwError } from 'rxjs';
import { CaptchaService } from './captcha.service';
import { DataService } from './data.service';

// TODO : test behaviour when captcha validation fail
describe("AuthService unit tests", () =>  {
    let authService: AuthService;
    let dataServiceMock: jest.Mocked<DataService>;
    let captchaServiceMock: jest.Mocked<CaptchaService>;
    
    beforeEach(async () => {
        captchaServiceMock = {
          getCaptchaToken: jest.fn().mockReturnValue(of('token'))
        } as unknown as jest.Mocked<CaptchaService>;

        dataServiceMock = {
            post: jest.fn()
        } as unknown as jest.Mocked<DataService>;

        jest.spyOn(captchaServiceMock, 'getCaptchaToken').mockReturnValue(of('token'));

        authService = new AuthService(dataServiceMock, captchaServiceMock);
    });

    afterEach(() => {
      jest.resetAllMocks();
    });

    it('should post login request and return session information as an observable', (done) => {
      const mockSessionInfo: SessionInformation = {email: "john.doe@example.com", username: "johndoe", role: "USER"};
      const loginRequest: LoginRequest = {email: "john.doe@example.com", password: "testpassword"};
      dataServiceMock.post.mockReturnValue(of(mockSessionInfo));
      
      authService.login(loginRequest).subscribe(
          {
              next: response =>  {
              expect(response).toEqual(mockSessionInfo);
              done();
          }}
      );
    })

    it('should post register request and return void as an observable', (done) => {
      const registrationRequest: RegistrationRequest = {email: "john.doe@example.com", password: "testpassword", username: "username", firstName: "firstName", lastName: "lastName"};
      dataServiceMock.post.mockReturnValue(of(null));

      authService.register(registrationRequest).subscribe(response => {
          expect(response).toBeNull();
          done()
      });
    })

    it('should get http error on login failed at network level', (done) => {
        // Create mock ProgressEvent with type `error`, raised when something goes wrong
      // at network level. e.g. Connection timeout, DNS error, offline, etc.
      const mockError = new ProgressEvent('error');
      const loginRequest: LoginRequest = {email: "john.doe@example.com", password: "testpassword"};
      dataServiceMock.post.mockReturnValue(throwError(() => new HttpErrorResponse({error: mockError})));
      
      authService.login(loginRequest).subscribe(
          {
              next: () =>  fail('should have failed with http error'),
              error: (error: HttpErrorResponse) => {
                  expect(error.error).toEqual(mockError);
                  done()
              }
          }
      );
    })

    it('should get http error on register failed at network level', (done) => {
      // Create mock ProgressEvent with type `error`, raised when something goes wrong
      // at network level. e.g. Connection timeout, DNS error, offline, etc.
      const mockError = new ProgressEvent('error');
      const registrationRequest: RegistrationRequest = {email: "john.doe@example.com", password: "testpassword", username: "username", firstName: "firstName", lastName: "lastName"};
      dataServiceMock.post.mockReturnValue(throwError(() => new HttpErrorResponse({error: mockError})));

      authService.register(registrationRequest).subscribe({
          next: () => fail('should have failed with http error'),
          error: (error: HttpErrorResponse) => {
              expect(error.error).toBe(mockError);
              done()
          }
      });
    })

    it('should get http error on logout failed at network level', (done) => {
      // Create mock ProgressEvent with type `error`, raised when something goes wrong
      // at network level. e.g. Connection timeout, DNS error, offline, etc.
      const mockError = new ProgressEvent('error');
      dataServiceMock.post.mockReturnValue(throwError(() => new HttpErrorResponse({error: mockError})));

      authService.logout().subscribe({
      next: () => fail('should have failed with http error'),
          error: (error: HttpErrorResponse) => {
              expect(error.error).toBe(mockError);
              done()
          }
      });
    });

    it('should logout', (done) => {
      dataServiceMock.post.mockReturnValue(of());
      authService.logout().subscribe({
          next: () => {
              expect(true).toBe(true);
            },
            complete: () => {
              expect(true).toBe(true);
              done()
            },
      });
    });

    it('should get http error on refresh token failed', (done) => {
      // Create mock ProgressEvent with type `error`, raised when something goes wrong
      // at network level. e.g. Connection timeout, DNS error, offline, etc.
      const mockError = new ProgressEvent('error');
      dataServiceMock.post.mockReturnValue(throwError(() => new HttpErrorResponse({error: mockError})));

      authService.refreshToken().subscribe({
      next: () => fail('should have failed with http error'),
          error: (error: HttpErrorResponse) => {
              expect(error.error).toBe(mockError);
              done()
          }
      });
    });

    it('should refresh token', (done) => {
      const mockSessionInfo: SessionInformation = {email: "john.doe@example.com", username: "johndoe", role: "USER"};
      dataServiceMock.post.mockReturnValue(of(mockSessionInfo));
      authService.refreshToken().subscribe(response => {
          expect(response).toEqual(mockSessionInfo);
          done()
      });
    });
})