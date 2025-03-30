import { Component } from '@angular/core';
import { SessionService } from '../../../core/services/session.service';
import { BehaviorSubject, catchError, throwError } from 'rxjs';
import { SessionInformation } from '../../../core/model/session-information.interface';
import { AsyncPipe } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-me',
  imports: [AsyncPipe],
  templateUrl: './me.component.html',
  styleUrl: './me.component.scss'
})
export class MeComponent {

  protected sessionInformation: BehaviorSubject<SessionInformation|null>;

  constructor(private authService: AuthService, private sessionService: SessionService, private router: Router) {
    this.sessionInformation = sessionService.$getSessionInformation();
  }

  public logout(): void {
    this.authService.logout()
            .pipe(
              catchError(
                () => {
                  return throwError(() => new Error(
                    'Logout failed.'
                  ));
                }
              )
            )
            .subscribe(
              () => {
                this.sessionService.logOut();
                this.router.navigate([''])
              }
            )
  }
}