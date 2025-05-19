import { EmailValidationComponent } from './email-validation.component';
import { of, throwError } from 'rxjs';

describe('EmailValidationComponent', () => {
  let component: EmailValidationComponent;

  const userServiceMock = {
    validateEmail: jest.fn(),
  };

  const routerMock = {
    navigate: jest.fn(),
  };

  const notificationServiceMock = {
    confirmation: jest.fn(),
  };

  const sessionServiceMock = {
    isLogged: jest.fn(),
  };

  const activatedRouteMock = {
    queryParams: of({ code: 'validCode123' }),
  };

  beforeEach(() => {
    jest.clearAllMocks();

    component = new EmailValidationComponent(
      userServiceMock as any,
      activatedRouteMock as any,
      routerMock as any,
      notificationServiceMock as any,
      sessionServiceMock as any,
    );
  });

  it('should navigate to home if code is missing', () => {
    const noCodeRoute = {
      queryParams: of({}),
    };

    component = new EmailValidationComponent(
      userServiceMock as any,
      noCodeRoute as any,
      routerMock as any,
      notificationServiceMock as any,
      sessionServiceMock as any,
    );

    component.ngOnInit();

    expect(routerMock.navigate).toHaveBeenCalledWith(['']);
  });

  it('should validate email and redirect on success', () => {
    userServiceMock.validateEmail.mockReturnValueOnce(of(undefined));
    sessionServiceMock.isLogged.mockReturnValueOnce(true);

    component.ngOnInit();

    expect(userServiceMock.validateEmail).toHaveBeenCalledWith({ code: 'validCode123' });
    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith(
      $localize`:@@info.email.validated:Your email has been validated.`
    );
    expect(routerMock.navigate).toHaveBeenCalledWith(['/jobs']);
  });

  it('should redirect to /login on validation error if not logged in', () => {
    sessionServiceMock.isLogged.mockReturnValueOnce(false);
    userServiceMock.validateEmail.mockReturnValueOnce(
      throwError(() => new Error('Email validation failed'))
    );

    component.ngOnInit();

    expect(userServiceMock.validateEmail).toHaveBeenCalledWith({ code: 'validCode123' });
    expect(notificationServiceMock.confirmation).not.toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should redirect to /jobs on validation error if logged in', () => {
    sessionServiceMock.isLogged.mockReturnValueOnce(true);
    userServiceMock.validateEmail.mockReturnValueOnce(
      throwError(() => new Error('Email validation failed'))
    );

    component.ngOnInit();

    expect(userServiceMock.validateEmail).toHaveBeenCalledWith({ code: 'validCode123' });
    expect(notificationServiceMock.confirmation).not.toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/jobs']);
  });
});
