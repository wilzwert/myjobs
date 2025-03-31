import { Component } from '@angular/core';
import { BehaviorSubject, catchError, Observable, throwError } from 'rxjs';
import { SessionInformation } from '../../../core/model/session-information.interface';
import { AsyncPipe, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { User } from '../../../core/model/user.interface';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { SessionService } from '../../../core/services/session.service';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatButton } from '@angular/material/button';
import { ModalService } from '../../../core/services/modal.service';

@Component({
  selector: 'app-me',
  imports: [AsyncPipe, DatePipe, MatCard, MatCardContent, MatButton],
  templateUrl: './me.component.html',
  styleUrl: './me.component.scss'
})
export class MeComponent {

  protected user$: Observable<User>;

  constructor(private userService: UserService, private authService: AuthService, private sessionService: SessionService, private router: Router, private modalService: ModalService) {
    this.user$ = this.userService.getUser();
  }

  public changePassword() :void {
    this.modalService.openPasswordModal(() => {});
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