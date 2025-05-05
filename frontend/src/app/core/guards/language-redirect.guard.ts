import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { LocaleService } from '../services/locale.service';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LanguageRedirectGuard implements CanActivate {
  constructor(private localeService: LocaleService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.localeService.handleLanguageRedirection();
    // return true;
  }
}