import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { LocaleService } from '../services/locale.service';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LanguageRedirectGuard implements CanActivate {
  constructor(private localeService: LocaleService) {
    console.log('construct language redirect guard');
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean |UrlTree> {
    alert('canActivate');
    let result : boolean | UrlTree = this.localeService.handleLanguageRedirection();
    console.log("canActivate ? ",result);
    return of(result);
  }
  
}
