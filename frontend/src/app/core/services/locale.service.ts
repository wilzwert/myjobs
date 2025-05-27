import { Injectable } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import localeEn from '@angular/common/locales/en';
import { BehaviorSubject, catchError, distinctUntilChanged, EMPTY, filter, firstValueFrom, from, map, Observable, of, switchMap, tap } from 'rxjs';
import { UserService } from './user.service';
import { SessionService } from './session.service';
import { CookieService } from 'ngx-cookie-service'
import { AVAILABLE_LANGS } from '@lang/lang';

@Injectable({
  providedIn: 'root'
})
export class LocaleService {

  private static UNKNOWN_LANG = 'none';
  private supportedLangs = ['fr', 'en'];
  private defaultLang = 'en';
  private _locale$ = new BehaviorSubject<string>(LocaleService.UNKNOWN_LANG);
  private initialLoadHandled = false;
  private isInitialized = false;

  constructor(private sessionService: SessionService, private userService: UserService, private cookieService: CookieService) {}

  private save(lang: string): Observable<void> {
    // cookie will allow access to the preferred lang from the front web server (nginx, cloudfront...)
    this.cookieService.delete('lang');
    this.cookieService.set('lang', lang, {expires: 365, path: '/'});
    
    // if user is loggedin, save their lang
    return this.sessionService.isLogged() 
      ? this.userService.saveUserLang(lang).pipe(catchError(() => EMPTY))
      : of(void 0);
  }

  private registerLocales() :void {
    registerLocaleData(localeFr, 'fr');
    registerLocaleData(localeEn, 'en');
  }

  private handleLangChange(lang: string) :Observable<void> {
    // no change handling before initial load is done
    if (!this.initialLoadHandled) return of(void 0);
    // if lang is not available or already is the current lang, no need to do anything
    if(!AVAILABLE_LANGS.includes(lang) || this.getCurrentLangFromUrl() === lang) {
      return of(void 0);
    }
    // saves the lang and redirects
    return this.save(lang).pipe(
      tap(() => {
        this.redirectTo(this.buildRedirectUrl(lang));
      })
    );
  }

  private observeLocaleChanges(): void {
    // observe locale changes to update user if needed
    this._locale$
      .pipe(
        // don't take unknown lang ie don't handle lang before it is resolved
        filter((l) => l !== LocaleService.UNKNOWN_LANG),
        distinctUntilChanged(), // avoid duplicates
        switchMap((lang) => this.handleLangChange(lang))
      )
      .subscribe();
  }

  async init() {
    this.registerLocales();
    this.observeLocaleChanges();
  }

  private redirectTo(url: string) :void {
    window.location.assign(url);
  }

  private async resolveDefaultLocale() : Promise<string> {
    // get lang from the current user if logged in
    if(this.sessionService.isLogged()) {
      let user = await firstValueFrom(this.userService.getUser());
      if(user !== null && user.lang !== null) {
        return user.lang!.toString().toLowerCase();
      }
    }

    // get lang from cookie
    const lang = this.cookieService.get('lang');
    if(lang !== '') {
      return lang;
    }

    // as a fallback, get lang from browser
    const browserLang = navigator.language.split('-')[0]; // e.g.: 'fr-FR' → 'fr'
    return (this.supportedLangs.includes(browserLang) ? browserLang : this.defaultLang).toLowerCase();
  }

  get locale$() {
    return this._locale$.asObservable();
  }

  get currentLocale(): string {
    return this._locale$.value;
  }

  public changeLocale(lang: string) {
    this._locale$.next(lang);
  }

  private buildRedirectUrl(lang: string): string {
    const path = location.pathname.replace(/^\/(fr|en)/, '');
    return `/${lang}${path}`;
  }
  
  private getCurrentLangFromUrl(): string {
    const match = window.location.pathname.match(/^\/(fr|en)/);
    return match?.[1] ?? '';
  }

  public handle(): Observable<boolean> {
    if (!this.isInitialized) {
      this.init();
      this.isInitialized = true;
    }

    return from(this.resolveDefaultLocale()).pipe(
      tap((lang) => {
        this.initialLoadHandled = true;
        this.changeLocale(lang);
      }),
      map(() => true)
    );
  }

}