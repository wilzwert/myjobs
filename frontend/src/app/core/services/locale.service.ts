import { Injectable } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import localeEn from '@angular/common/locales/en';
import { BehaviorSubject, catchError, distinctUntilChanged, EMPTY, filter, firstValueFrom, map, Observable, of, switchMap, tap } from 'rxjs';
import { UserService } from './user.service';
import { SessionService } from './session.service';
import { Router } from '@angular/router';
import { AVAILABLE_LANGS } from '../../../lang/lang';

@Injectable({
  providedIn: 'root'
})
export class LocaleService {

  private static UNKNOWN_LANG = 'none';
  private supportedLangs = ['fr', 'en'];
  private defaultLang = 'en';
  private _locale$ = new BehaviorSubject<string>(LocaleService.UNKNOWN_LANG);
  private initialRedirectHandled = false;

  constructor(private sessionService: SessionService, private userService: UserService, private router: Router) {
    this.init();
  }

  private save(lang: string): Observable<void> {
    // we are currently handling initial redirection
    // saving lang makes no sense at this point, as we are not switching langs
    // FIXME: this is not very clean but it works for now
    if(!this.initialRedirectHandled) {
      return of(void 0);
    }

    // TODO : store in a cookie instead of local storage
    // cookie will allow access to the preferred lang from the front web server (nginx, cloudfront...)
    localStorage.setItem("lang", lang);
  
    if (this.sessionService.isLogged()) {
      return this.userService.saveUserLang(lang).pipe(
        catchError(() => EMPTY)
      );
    } else {
      return of(void 0);
    }
  }
  

  async init() {
    registerLocaleData(localeFr, 'fr');
    registerLocaleData(localeEn, 'en');
    
    // observe locale changes to update user if needed
    this._locale$
      .pipe(
        // don't take unknown lang ie don't handle lang before it is resolved
        filter((l) => l !== LocaleService.UNKNOWN_LANG),
        distinctUntilChanged(), // avoid duplicates
        switchMap((lang) => {
          // if lang is not available or already is the current lang, no need to do anything
          if(!AVAILABLE_LANGS.includes(lang) || this.getCurrentLangFromUrl() === lang) {
            return of(lang);
          }
          // saves the lang and redirects
          return this.save(lang).pipe(
            tap(() => {
                window.location.href = this.buildRedirectUrl(lang);
            })
          );
        })
      )
      .subscribe();
  }

  private async resolveDefaultLocale() : Promise<string> {
    // get lang from the current user if logged in
    if(this.sessionService.isLogged()) {
      let user = await firstValueFrom(this.userService.getUser());
      if(user !== null && user.lang !== null) {
        return user.lang!.toString().toLowerCase();
      }
    }

    // TODO retrieve from cookie instead of localstorage
    const lang = localStorage.getItem("lang");
    if(lang !== null) {
      return lang;
    }
    
    const browserLang = navigator.language.split('-')[0]; // e.g.: 'fr-FR' → 'fr'
    return (this.supportedLangs.includes(browserLang) ? browserLang : this.defaultLang).toLowerCase();
  }

  get locale$() {
    return this._locale$.asObservable();
  }

  get currentLocale(): string {
    return this._locale$.value;
  }

  changeLocale(lang: string) {
    this._locale$.next(lang);
  }

  private buildRedirectUrl(lang: string): string {
    const path = location.pathname.replace(/^\/(fr|en)/, '');
    return `/${lang}${path}`;
  }

  handleLanguageRedirection(): Observable<boolean> {
    this.resolveDefaultLocale().then((lang) => {this.changeLocale(lang)});
    return this.locale$.pipe(
      filter((l) => l !== LocaleService.UNKNOWN_LANG),
      switchMap(() => {
        // initial redirect handled, lets change state to allow further lang switches and saving
        this.initialRedirectHandled = true;
        return of(true);
      })
    );
  }
  
  getCurrentLangFromUrl(): string {
    const match = window.location.pathname.match(/^\/(fr|en)/);
    return match?.[1] ?? '';
  }
}