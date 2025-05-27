import { MenuComponent } from './menu.component';
import { of, Subject, throwError } from 'rxjs';
import { NavigationEnd } from '@angular/router';

describe('MenuComponent', () => {
  let component: MenuComponent;
  let sessionServiceMock: any;
  let authServiceMock: any;
  let routerMock: any;
  let localeServiceMock: any;

  beforeEach(() => {
    sessionServiceMock = {
      $isLogged: jest.fn().mockReturnValue(of(true)),
      logOut: jest.fn()
    };

    authServiceMock = {
      logout: jest.fn().mockReturnValue(of(null))
    };

    routerMock = {
      events: new Subject(),
      navigate: jest.fn()
    };

    localeServiceMock = {
      currentLocale: 'fr',
      changeLocale: jest.fn()
    };

    component = new MenuComponent(
      sessionServiceMock,
      authServiceMock,
      routerMock,
      localeServiceMock
    );
  });

  it('should close nav on NavigationEnd', () => {
    component.menuOpen = true;
    component.ngOnInit();

    // simulate navigation end
    routerMock.events.next(new NavigationEnd(1, '/previous', '/current'));

    expect(component.menuOpen).toBe(false);
  });

  it('should return current language from localeService', () => {
    expect(component.lang).toBe('fr');
  });

  it('should call changeLocale when changeLang is called', () => {
    component.changeLang('en');
    expect(localeServiceMock.changeLocale).toHaveBeenCalledWith('en');
  });

  it('should handle logout correctly', () => {
    component.logout();

    expect(authServiceMock.logout).toHaveBeenCalled();
    expect(sessionServiceMock.logOut).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['']);
  });

  it('should not crash on logout error', () => {
    authServiceMock.logout.mockReturnValueOnce(throwError(() => new Error('logout failed')));

    component.logout();

    expect(authServiceMock.logout).toHaveBeenCalled();
    // on ne teste pas plus ici car le throwError est capturé silencieusement
  });

  it('should emit destroy$ on ngOnDestroy', () => {
    const nextSpy = jest.spyOn(component['destroy$'], 'next');
    component.ngOnDestroy();
    expect(nextSpy).toHaveBeenCalledWith(true);
  });
});
