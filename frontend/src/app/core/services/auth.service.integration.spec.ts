import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import { HttpTestingController, provideHttpClientTesting, TestRequest } from '@angular/common/http/testing';
import { LoginRequest } from '@core/model/login-request.interface';
import { RegistrationRequest } from '@core/model/registration-request.interface';
import { SessionInformation } from '@core/model/session-information.interface';
import { of } from 'rxjs';
import { CaptchaService } from './captcha.service';

describe("AuthService integration tests", () =>  {
    let service: AuthService;
    let mockHttpController: HttpTestingController;

    beforeEach(async () => {
        const mockRecaptcha = {
            getCaptchaToken: jest.fn(() => {return of("captcha_token");}),
          };

        TestBed.configureTestingModule({
            providers: [
                AuthService,
                provideHttpClient(),
                provideHttpClientTesting(),
                {provide: CaptchaService, useValue: mockRecaptcha}
            ],
            imports: [],
        });
    
        service = TestBed.inject(AuthService);
        mockHttpController = TestBed.inject(HttpTestingController);
      });

      afterEach(() =>  {
        mockHttpController.verify()
      })

      it('should post login request and return session information as an observable', (done) => {
        const mockSessionInfo: SessionInformation = {email: "john.doe@example.com", username: "johndoe", role: "USER"};
        const loginRequest: LoginRequest = {email: "john.doe@example.com", password: "testpassword"};
        
        service.login(loginRequest).subscribe(
            {
                next: response =>  {
                expect(response).toEqual(mockSessionInfo);
                done();
            }}
        );

        const testRequest: TestRequest = mockHttpController.expectOne("/api/auth/login");
        expect(testRequest.request.method).toEqual("POST");
        expect(testRequest.request.body).toEqual(loginRequest);
        testRequest.flush(mockSessionInfo);
      })

      
      it('should post register request and return void as an observable', (done) => {
        const registrationRequest: RegistrationRequest = {email: "john.doe@example.com", password: "testpassword", username: "username", firstName: "firstName", lastName: "lastName"};
        service.register(registrationRequest).subscribe(response => {
            expect(response).toBeNull();
            done()
        });

        const testRequest: TestRequest = mockHttpController.expectOne("/api/auth/register");
        expect(testRequest.request.method).toEqual("POST");
        expect(testRequest.request.body).toEqual(registrationRequest);
        testRequest.flush(null);
      })

      it('should get http error on login failed at network level', (done) => {
         // Create mock ProgressEvent with type `error`, raised when something goes wrong
        // at network level. e.g. Connection timeout, DNS error, offline, etc.
        const mockError = new ProgressEvent('error');
        const loginRequest: LoginRequest = {email: "john.doe@example.com", password: "testpassword"};
        
        service.login(loginRequest).subscribe(
            {
                next: () =>  fail('should have failed with http error'),
                error: (error: HttpErrorResponse) => {
                    expect(error.error).toBe(mockError);
                    done()
                }
            }
        );

        const testRequest: TestRequest = mockHttpController.expectOne("/api/auth/login");
        expect(testRequest.request.method).toEqual("POST");
        expect(testRequest.request.body).toEqual(loginRequest);
        testRequest.error(mockError);
      })

      it('should get http error on register failed at network level', (done) => {
        // Create mock ProgressEvent with type `error`, raised when something goes wrong
       // at network level. e.g. Connection timeout, DNS error, offline, etc.
       const mockError = new ProgressEvent('error');
       const registrationRequest: RegistrationRequest = {email: "john.doe@example.com", password: "testpassword", username: "username", firstName: "firstName", lastName: "lastName"};
       service.register(registrationRequest).subscribe({
            next: () => fail('should have failed with http error'),
            error: (error: HttpErrorResponse) => {
                expect(error.error).toBe(mockError);
                done()
            }
        });

        const testRequest: TestRequest = mockHttpController.expectOne("/api/auth/register");
        expect(testRequest.request.method).toEqual("POST");
        expect(testRequest.request.body).toEqual(registrationRequest);
        testRequest.error(mockError);
     })

     it('should get http error on logout failed at network level', (done) => {
        // Create mock ProgressEvent with type `error`, raised when something goes wrong
       // at network level. e.g. Connection timeout, DNS error, offline, etc.
       const mockError = new ProgressEvent('error');

       service.logout().subscribe({
        next: () => fail('should have failed with http error'),
            error: (error: HttpErrorResponse) => {
                expect(error.error).toBe(mockError);
                done()
            }
       });

       const testRequest: TestRequest = mockHttpController.expectOne("/api/auth/logout");
       expect(testRequest.request.method).toEqual("POST");
       expect(testRequest.request.body).toBeNull();
       testRequest.error(mockError);
     });

     it('should logout', (done) => {
        service.logout().subscribe(response => {
            expect(response).toBeNull();
            done()
        });

       const testRequest: TestRequest = mockHttpController.expectOne("/api/auth/logout");
       expect(testRequest.request.method).toEqual("POST");
       expect(testRequest.request.body).toBeNull();
       testRequest.flush(null);
     });

     it('should get http error on refresh token failed', (done) => {
        // Create mock ProgressEvent with type `error`, raised when something goes wrong
       // at network level. e.g. Connection timeout, DNS error, offline, etc.
       const mockError = new ProgressEvent('error');

       service.refreshToken().subscribe({
        next: () => fail('should have failed with http error'),
            error: (error: HttpErrorResponse) => {
                expect(error.error).toBe(mockError);
                done()
            }
       });

       const testRequest: TestRequest = mockHttpController.expectOne("/api/auth/refresh-token");
       expect(testRequest.request.method).toEqual("POST");
       expect(testRequest.request.body).toBeNull();
       testRequest.error(mockError);
     });

     it('should refresh token', (done) => {
        const mockSessionInfo: SessionInformation = {email: "john.doe@example.com", username: "johndoe", role: "USER"};
        service.refreshToken().subscribe(response => {
            expect(response).toEqual(mockSessionInfo);
            done()
        });

       const testRequest: TestRequest = mockHttpController.expectOne("/api/auth/refresh-token");
       expect(testRequest.request.method).toEqual("POST");
       expect(testRequest.request.body).toBeNull();
       testRequest.flush(mockSessionInfo);
     });
})