import { Injectable } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import localeEn from '@angular/common/locales/en';
import { BehaviorSubject, catchError, distinctUntilChanged, EMPTY, filter, firstValueFrom, map, Observable, of, switchMap, tap } from 'rxjs';
import { UserService } from './user.service';
import { SessionService } from './session.service';
import { Router, UrlTree } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class LocaleService {

  private supportedLangs = ['fr', 'en'];
  private defaultLang = 'en';
  private _locale$ = new BehaviorSubject<string>('none');

  constructor(private sessionService: SessionService, private userService: UserService, private router: Router) {
    // sets default conf
    registerLocaleData(localeFr, 'fr');
    registerLocaleData(localeEn, 'en');
    this.init();
  }

  private save(lang: string): Observable<void> {
    localStorage.setItem("lang", lang);
  
    if (this.sessionService.isLogged()) {
      return this.userService.saveUserLang(lang).pipe(
        catchError(() => EMPTY)
      );
    } else {
      return of(void 0);
    }
  }
  

  init() {
    // observe locale changes to update user if needed
    this._locale$
      .pipe(
        filter((l) => l !== 'none'),
        distinctUntilChanged(), // avoid duplicates
        switchMap((lang) => {
          return this.save(lang).pipe(
            tap(() => this.router.navigate([this.buildRedirectUrl(lang)]))
          );
        })
      )
      .subscribe();
      
    this.resolveDefaultLocale().then((lang) => {this.changeLocale(lang);});
  }

  private async resolveDefaultLocale() : Promise<string> {
    if(this.sessionService.isLogged()) {
      let user = await firstValueFrom(this.userService.getUser());
      if(user !== null && user.lang !== null) {
        return user.lang!.toString().toLowerCase();
      }
    }

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
    alert(lang);
    alert(location.pathname);
    const path = location.pathname.replace(/^\/(fr|en)/, '');
    alert(`goto /${lang}${path}`);
    return `/${lang}${path}`;
  }

  handleLanguageRedirection(): Observable<boolean | UrlTree> {
    const preferredLang = this.currentLocale;
    const currentLang = this.getCurrentLangFromUrl();

    if (preferredLang === currentLang) {
      return of(true); // pas de redirection nécessaire
    }

    const redirectUrl = this.buildRedirectUrl(preferredLang);

    return of(this.router.parseUrl(redirectUrl));
  }
  
  getCurrentLangFromUrl(): string {
    const match = window.location.pathname.match(/^\/(fr|en)/);
    return match?.[1] ?? '';
  }
}