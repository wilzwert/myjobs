import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { HTTP_INTERCEPTORS, HttpClient, HttpErrorResponse, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { JwtInterceptor } from './jwt.interceptor';
import { SessionService } from '@core/services/session.service';
import { AuthService } from '@core/services/auth.service';
import { ErrorProcessorService } from '@core/services/error-processor.service';
import { of, throwError } from 'rxjs';
import { SessionInformation } from '@core/model/session-information.interface';

describe('JwtInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let sessionService: jest.Mocked<SessionService>;
  let authService: jest.Mocked<AuthService>;
  let errorProcessorService: jest.Mocked<ErrorProcessorService>;

  beforeEach(() => {
    sessionService = {
      isLogged: jest.fn(),
      logIn: jest.fn(),
      logOut: jest.fn(),
    } as any;

    authService = {
      refreshToken: jest.fn()
    } as any;

    errorProcessorService = {
      processError: jest.fn().mockImplementation((e) => throwError(() => e))
    } as any;

    TestBed.configureTestingModule({
      imports: [],
      providers: [
        { provide: SessionService, useValue: sessionService },
        { provide: AuthService, useValue: authService },
        { provide: ErrorProcessorService, useValue: errorProcessorService },
        {
          provide: HTTP_INTERCEPTORS,
          useClass: JwtInterceptor,
          multi: true,
        },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
      ],
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should refresh token on 401 and retry request', () => {
    const fakeSession: SessionInformation = {
      email: 'user@example.com',
      username: 'user',
      role: 'USER'
    };

    sessionService.isLogged.mockReturnValue(true);
    authService.refreshToken.mockReturnValue(of(fakeSession));

    http.get('/api/resource').subscribe();

    const req1 = httpMock.expectOne('/api/resource');
    req1.flush(null, { status: 401, statusText: 'Unauthorized' });

    const req2 = httpMock.expectOne('/api/resource');
    expect(req2.request.url).toBe('/api/resource');

    expect(sessionService.logIn).toHaveBeenCalledWith(fakeSession);
  });

  it('should logout and call processError on refresh failure', () => {
    sessionService.isLogged.mockReturnValue(true);
    authService.refreshToken.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 403 })));

    http.get('/api/resource').subscribe({
      error: (err) => {
        expect(sessionService.logOut).toHaveBeenCalled();
        expect(errorProcessorService.processError).toHaveBeenCalled();
        expect(err.status).toBe(403);
      }
    });

    const req1 = httpMock.expectOne('/api/resource');
    req1.flush(null, { status: 401, statusText: 'Unauthorized' });
  });

  it('should bypass interceptor if not logged in', () => {
    sessionService.isLogged.mockReturnValue(false);

    http.get('/api/resource').subscribe();

    const req = httpMock.expectOne('/api/resource');
    expect(req.request.url).toBe('/api/resource');
  });

  it('should bypass interceptor for non-api urls', () => {
    sessionService.isLogged.mockReturnValue(true);

    http.get('/other/resource').subscribe();

    const req = httpMock.expectOne('/other/resource');
    expect(req.request.url).toBe('/other/resource');
  });
});
