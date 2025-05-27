import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewPasswordComponent } from './new-password.component';
import { ActivatedRoute, Router } from '@angular/router';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideScReCaptchaSettings } from '@semantic-components/re-captcha';
import { environment } from '@environments/environment';
import { ErrorInterceptor } from '@core/interceptors/error.interceptor';
import { ErrorProcessorService } from '@core/services/error-processor.service';
import { of } from 'rxjs';
import { NotificationService } from '@core/services/notification.service';
import { CaptchaService } from '@core/services/captcha.service';

describe('NewPasswordComponent', () => {
  let component: NewPasswordComponent;
  let fixture: ComponentFixture<NewPasswordComponent>;
  let activatedRouteMock: jest.Mocked<ActivatedRoute>;
  let errorProcessorService: ErrorProcessorService;
  let httpTestingController: HttpTestingController;
  let notificationService: NotificationService;
  let captchaServiceMock: jest.Mocked<CaptchaService>;
  let router: Router;

  beforeEach(async () => {

    activatedRouteMock = { snapshot: {}, params: {}, queryParams: of({'token':'reset-password-token'}) } as unknown as jest.Mocked<ActivatedRoute>;
    captchaServiceMock = {
      getCaptchaToken: jest.fn()
    } as unknown as jest.Mocked<CaptchaService>;

    await TestBed.configureTestingModule({
      imports: [NewPasswordComponent],
      providers: [
        ErrorProcessorService,
        NotificationService,
        { provide: ActivatedRoute, useValue: activatedRouteMock},
        { provide: CaptchaService, useValue: captchaServiceMock},
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
        provideScReCaptchaSettings({
          v3SiteKey: environment.recaptcha_key,
          languageCode: 'fr',
        }),
        { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewPasswordComponent);
    component = fixture.componentInstance;
    httpTestingController = TestBed.inject(HttpTestingController);
    errorProcessorService = TestBed.inject(ErrorProcessorService);
    notificationService = TestBed.inject(NotificationService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit successfully and redirect to login', () => {
    const notificationSpy = jest.spyOn(notificationService, 'confirmation');
    const routerSpy = jest.spyOn(router, 'navigate');

    captchaServiceMock.getCaptchaToken.mockReturnValue(of('token'));
    // Arrange
    component.form.patchValue({
      password: 'Validpass1!',
      passwordConfirmation: 'Validpass1!'
    });

    // Act
    component.submit();

    const req = httpTestingController.expectOne('/api/user/password'); 
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      password: 'Validpass1!',
      token: 'reset-password-token'
    });

    req.flush({}); // simule une réponse HTTP 200

    expect(notificationSpy).toHaveBeenCalled();
    expect(routerSpy).toHaveBeenCalledWith(['/login']);
    expect(component.isSubmitting).toBe(false);
  });

  it('should handle error if userService.newPassword fails', () => {
    const errorProcessorSpy = jest.spyOn(errorProcessorService, 'processError');
    const notificationSpy = jest.spyOn(notificationService, 'confirmation');
    const routerSpy = jest.spyOn(router, 'navigate');

    // Arrange
    component.form.patchValue({
      password: 'Validpass1!',
      passwordConfirmation: 'Validpass1!'
    });
    captchaServiceMock.getCaptchaToken.mockReturnValue(of('token'));

    component.submit();

    const req = httpTestingController.expectOne('/api/user/password'); 
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      password: 'Validpass1!',
      token: 'reset-password-token'
    });


    req.flush({ message: 'Unauthorized' }, { status: 401, statusText: 'Unauthorized' });

    expect(errorProcessorSpy).toHaveBeenCalled();
    expect(notificationSpy).not.toHaveBeenCalled();
    expect(routerSpy).not.toHaveBeenCalled();
    expect(component.isSubmitting).toBe(false);
  });
});
