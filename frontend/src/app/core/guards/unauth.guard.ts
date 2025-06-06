import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from '@angular/router';
import { SessionService } from '@core/services/session.service';
import { map, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UnauthGuard implements CanActivate {
  constructor(private router: Router, private sessionService: SessionService) {}

  canActivate(): boolean {
    if(this.sessionService.isLogged()) {
      this.router.navigate(['jobs']);
    }
    return !this.sessionService.isLogged();
  }
}
