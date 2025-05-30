import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from '@angular/router';
import { SessionService } from '@core/services/session.service';
import { map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private sessionService: SessionService) {}

  canActivate(): boolean {
    if(!this.sessionService.isLogged()) {
      this.router.navigate(["/login"]);
    }
    return this.sessionService.isLogged();
  }
  
}
