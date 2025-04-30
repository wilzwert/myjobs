import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, RouterStateSnapshot, UrlTree } from '@angular/router';
import { LocaleService } from '../services/locale.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LanguageRedirectGuard implements CanActivate {
  constructor(private localeService: LocaleService) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.localeService.handleLanguageRedirection();
  }
  
}
