import { of } from 'rxjs';
import { LocaleService } from './locale.service';
import { SessionService } from './session.service';
import { UserService } from './user.service';
import { CookieService } from 'ngx-cookie-service';
import { User } from '@core/model/user.interface';
import { EmailStatus } from '@core/model/email-status';
import { Lang } from '@core/model/lang';

describe('LocaleService tests', () => {
  let service: LocaleService;
  
  let sessionServiceMock: jest.Mocked<SessionService>;
  let userServiceMock: jest.Mocked<UserService>;
  let cookieServiceMock: jest.Mocked<CookieService>;

  let userMock = { firstName: 'John', lastName: 'Doe', username: 'john', email: 'john@doe.com', lang: Lang.FR, emailStatus: EmailStatus.VALIDATED, createdAt: '' } as User;

  const locationMock = { href: '', assign: jest.fn() };
  global.window.location = locationMock as any;

  Object.defineProperty(window, 'location', {
    value: {
      ...window.location,
      assign: jest.fn(),
    },
    writable: true,
  });

  beforeEach(() => {
    sessionServiceMock = {
      isLogged: jest.fn()
    } as unknown as jest.Mocked<SessionService>;

    userServiceMock = {
      saveUserLang: jest.fn(),
      getUser: jest.fn()
    } as unknown as jest.Mocked<UserService>;

    cookieServiceMock = {
      delete: jest.fn(),
      set: jest.fn(),
      get: jest.fn()
    } as unknown as jest.Mocked<CookieService>;

    service = new LocaleService(sessionServiceMock, userServiceMock, cookieServiceMock);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should resolve user lang if logged in', async () => {
    sessionServiceMock.isLogged.mockReturnValue(true);
    userServiceMock.getUser.mockReturnValue(of(userMock));

    const lang = await service['resolveDefaultLocale']();

    expect(lang).toBe('fr');
  });

  it('should fallback to cookie lang if not logged in', async () => {
    sessionServiceMock.isLogged.mockReturnValue(false);
    cookieServiceMock.get.mockReturnValue('fr');

    const lang = await service['resolveDefaultLocale']();
    expect(lang).toBe('fr');
  });

  it('should fallback to browser lang if not logged in and no lang cookie', async () => {
    sessionServiceMock.isLogged.mockReturnValue(false);
    cookieServiceMock.get.mockReturnValue('');

    Object.defineProperty(navigator, 'language', {
      value: 'en-EN',
      configurable: true,
    });

    const lang = await service['resolveDefaultLocale']();
    expect(lang).toBe('en');
  });

  it('should save user lang when logged in', (done) => {
    sessionServiceMock.isLogged.mockReturnValue(true);
    userServiceMock.saveUserLang.mockReturnValue(of(undefined));

    // save method is private
    service['save']('en').subscribe(done);

    expect(cookieServiceMock.delete).toHaveBeenCalledWith('lang');
    expect(cookieServiceMock.set).toHaveBeenCalledWith('lang', 'en', { expires: 365, path: '/' });
    expect(userServiceMock.saveUserLang).toHaveBeenCalledWith('en');
  });

  it('should not save user lang when not logged in', (done) => {
    sessionServiceMock.isLogged.mockReturnValue(false);

    // save method is private
    service['save']('en').subscribe(done);

    expect(cookieServiceMock.delete).toHaveBeenCalledWith('lang');
    expect(cookieServiceMock.set).toHaveBeenCalledWith('lang', 'en', { expires: 365, path: '/' });
    expect(userServiceMock.saveUserLang).not.toHaveBeenCalledWith();
  });

  it('should save user lang and redirect on lang change if logged in', (done) => {
    sessionServiceMock.isLogged.mockReturnValue(true);
    userServiceMock.getUser.mockReturnValue(of(userMock));
    userServiceMock.saveUserLang.mockReturnValue(of(void 0));
    
  
    service.handle().subscribe(() => {
      service.changeLocale('fr');
  
      // un petit delay pour laisser le temps à observeLocaleChanges
      setTimeout(() => {
        expect(userServiceMock.saveUserLang).toHaveBeenCalledWith('fr');
        expect(cookieServiceMock.set).toHaveBeenCalledWith('lang', 'fr', { expires: 365, path: '/' });
        expect(window.location.assign).toHaveBeenCalledWith(expect.stringContaining('/fr'));
        done();
      }, 10);
    });
  });

  it('should set cookie and redirect without saving user lang if not logged in', (done) => {
    sessionServiceMock.isLogged.mockReturnValue(false);
  
    service.handle().subscribe(() => {
      service.changeLocale('fr');
  
      setTimeout(() => {
        expect(userServiceMock.saveUserLang).not.toHaveBeenCalled();
        expect(cookieServiceMock.set).toHaveBeenCalledWith('lang', 'fr', { expires: 365, path: '/' });
        expect(window.location.assign).toHaveBeenCalledWith(expect.stringContaining('/fr'));
        done();
      }, 10);
    });
  });

});
