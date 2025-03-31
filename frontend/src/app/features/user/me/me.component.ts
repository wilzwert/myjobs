import { Component } from '@angular/core';
import { BehaviorSubject, catchError, EMPTY, Observable, take, throwError } from 'rxjs';
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
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { ApiError } from '../../../core/errors/api-error';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-me',
  imports: [AsyncPipe, DatePipe, MatCard, MatCardContent, MatButton],
  templateUrl: './me.component.html',
  styleUrl: './me.component.scss'
})
export class MeComponent {

  protected user$: Observable<User>;

  constructor(private userService: UserService, private authService: AuthService, private sessionService: SessionService, private router: Router, private modalService: ModalService, private dialogService: ConfirmDialogService, private notificationService: NotificationService) {
    this.user$ = this.userService.getUser();
  }

  public changePassword() :void {
    this.modalService.openPasswordModal(() => {});
  }

  private endDeleteAccount() :void {
    this.sessionService.logOut();
    this.notificationService.confirmation("Your account has been deleted. Thank your for using MyJobs.");
    this.router.navigate([""]);
  }

  public confirmDeleteAccount() :void {
    // TODO delete user account
    this.userService.deleteUser().pipe(
      take(1),
      catchError((error: ApiError) => {
        this.notificationService.error(`An error occurred while deleting your account. ${error.message}`, error);
        return throwError(() => error);
      })
    ).subscribe(() => {
      // first we logout ; it will help prevent the jwt interceptor to try and re-authenticate
      this.sessionService.logOut(false);
      this.authService.logout().
        pipe(
        catchError(() =>  {
          this.endDeleteAccount();
          return EMPTY;
        })
      )
      .subscribe(this.endDeleteAccount.bind(this));
    });
  }

  public deleteAccount() :void {
    this.dialogService.openConfirmDialog("Delete your accout ? All data will be definitely deleted !", this.confirmDeleteAccount.bind(this));
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