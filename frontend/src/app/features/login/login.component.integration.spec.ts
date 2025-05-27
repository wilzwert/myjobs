import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { LoginComponent } from './login.component';
import { SessionService } from '@core/services/session.service';
import { AuthService } from '@core/services/auth.service';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideScReCaptchaSettings } from '@semantic-components/re-captcha';
import { environment } from '@environments/environment';
import { CaptchaService } from '@core/services/captcha.service';
import { of } from 'rxjs';
import { ErrorProcessorService } from '@core/services/error-processor.service';
import { ErrorInterceptor } from '@core/interceptors/error.interceptor';

describe('LoginComponent integration test', () => {
  let fixture: ComponentFixture<LoginComponent>;
  let component: LoginComponent;
  let httpTestingController: HttpTestingController;
  let captchaServiceMock: jest.Mocked<CaptchaService>;
  let sessionServiceSpy: { logIn: jest.Mock };
  let router: Router;
  let errorProcessorService: ErrorProcessorService;
  

  beforeEach(() => {
    // routerSpy = { navigate: jest.fn() };
    sessionServiceSpy = { logIn: jest.fn() };
    
    captchaServiceMock = {
      getCaptchaToken: jest.fn()
    } as unknown as jest.Mocked<CaptchaService>;

    TestBed.configureTestingModule({
      imports: [],
      providers: [
        AuthService,
        ErrorProcessorService,
        { provide: CaptchaService, useValue: captchaServiceMock},
        { provide: SessionService, useValue: sessionServiceSpy },
        { provide: ActivatedRoute, useValue: { snapshot: {}, params: {} } },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
        provideScReCaptchaSettings({
          v3SiteKey: environment.recaptcha_key,
          languageCode: 'fr',
        }),
        { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
      ]
    });

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    httpTestingController = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    errorProcessorService = TestBed.inject(ErrorProcessorService);
    
    // trigger initial data binding
    fixture.detectChanges();
  });

  afterEach(() => {
    httpTestingController.verify(); // Vérifie qu'aucune requête HTTP non consommée ne traîne
  });

  it('should login successfully and navigate to /jobs', () => {
    captchaServiceMock.getCaptchaToken.mockReturnValue(of('token'));
    component.form.setValue({ email: 'test@example.com', password: '1234' });
    const routerSpy = jest.spyOn(router, 'navigate');

    component.submit();

    // POST request to backend
    const req = httpTestingController.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');

    const mockResponse = { token: 'abc123', user: { id: 42, name: 'Test User' } };
    req.flush(mockResponse);

    expect(sessionServiceSpy.logIn).toHaveBeenCalledWith(mockResponse);
    expect(routerSpy).toHaveBeenCalledWith(['/jobs']);
    expect(component.isSubmitting).toBe(false);
  });
  
  it('should handle 401 Unauthorized error and reset isSubmitting', () => {
    captchaServiceMock.getCaptchaToken.mockReturnValue(of('token'));
    component.form.setValue({ email: 'wrong@example.com', password: 'badpass' });
    const routerSpy = jest.spyOn(router, 'navigate');
    const errorProcessorSpy = jest.spyOn(errorProcessorService, 'processError');

    component.submit();

    const req = httpTestingController.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');

    // Simulate HTTP 401 Unauthorized error
    req.flush({ message: 'Unauthorized' }, { status: 401, statusText: 'Unauthorized' });

    expect(component.isSubmitting).toBe(false);
    expect(routerSpy).not.toHaveBeenCalled();
    expect(sessionServiceSpy.logIn).not.toHaveBeenCalled();
    expect(errorProcessorSpy).toHaveBeenCalledWith(
      new Error(
        $localize `:@@error.login:Login failed`+'.'+' '+$localize `:@@info.login.verify:Please verify your email or password` +'.'
      )
    );
  });

  it('should handle not 401 error and reset isSubmitting', () => {
    captchaServiceMock.getCaptchaToken.mockReturnValue(of('token'));
    component.form.setValue({ email: 'wrong@example.com', password: 'badpass' });
    const routerSpy = jest.spyOn(router, 'navigate');
    const errorProcessorSpy = jest.spyOn(errorProcessorService, 'processError');

    component.submit();

    const req = httpTestingController.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');

    // Simule une erreur HTTP 401 Unauthorized
    req.flush({ message: 'Unauthorized' }, { status: 403, statusText: 'Forbidden' });

    expect(component.isSubmitting).toBe(false);
    expect(routerSpy).not.toHaveBeenCalled();
    expect(sessionServiceSpy.logIn).not.toHaveBeenCalled();
    expect(errorProcessorSpy).toHaveBeenCalledWith(
      new Error(
        $localize `:@@error.login:Login failed`+'.'
      )
    );
  });

  it('should not submit if already submitting', () => {
    component.isSubmitting = true;
    component.form.setValue({ email: 'test@example.com', password: '1234' });

    component.submit();

    // Aucune requête HTTP ne doit être émise
    httpTestingController.expectNone('/api/auth/login');
  });

  it('should not submit if form is invalid', () => {
    component.form.setValue({ email: 'invalid-email', password: '12' }); 

    component.submit();

    httpTestingController.expectNone('/api/auth/login');
    expect(component.isSubmitting).toBe(false);
  });
});