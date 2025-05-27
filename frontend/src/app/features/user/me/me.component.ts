import { Component } from '@angular/core';
import { catchError, EMPTY, Observable, take, throwError } from 'rxjs';
import { AsyncPipe, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { User } from '@core/model/user.interface';
import { UserService } from '@core/services/user.service';
import { AuthService } from '@core/services/auth.service';
import { SessionService } from '@core/services/session.service';
import { MatCard, MatCardActions, MatCardContent } from '@angular/material/card';
import { MatButton } from '@angular/material/button';
import { ModalService } from '@core/services/modal.service';
import { ConfirmDialogService } from '@core/services/confirm-dialog.service';
import { ApiError } from '@core/errors/api-error';
import { NotificationService } from '@core/services/notification.service';
import { EmailStatus } from '@core/model/email-status';
import { MatIcon } from '@angular/material/icon';
import { ComponentInputDomainData } from '@core/model/component-input-data.interface';

@Component({
  selector: 'app-me',
  imports: [AsyncPipe, DatePipe, MatCard, MatCardContent, MatButton, MatIcon, MatCardActions],
  templateUrl: './me.component.html',
  styleUrl: './me.component.scss'
})
export class MeComponent {

  protected user$: Observable<User>;
  protected EmailStatus = EmailStatus;

  constructor(private userService: UserService, private authService: AuthService, private sessionService: SessionService, private router: Router, private modalService: ModalService, private dialogService: ConfirmDialogService, private notificationService: NotificationService) {
    this.user$ = this.userService.getUser();
  }

  public changePassword() :void {
    this.modalService.openPasswordModal(() => {});
  }

  private endDeleteAccount() :void {
    this.sessionService.logOut();
    this.notificationService.confirmation($localize `:@@info.account_deleted:Your account has been deleted. Thank your for using MyJobs.`);
    this.router.navigate([""]);
  }

  public confirmDeleteAccount() :void {
    // TODO delete user account
    this.userService.deleteUser().pipe(
      take(1)
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
    this.dialogService.openConfirmDialog($localize `:@@warning.user.account_delete:Delete your accout ? All data will be definitely deleted !`, this.confirmDeleteAccount.bind(this));
  }

  public confirmSendVerificationEmail() {
    this.userService.sendVerificationMail()
      .subscribe(
        () => {
          this.notificationService.confirmation($localize `:@@info.user.validation_email_sent:The verification email has been sent ; please check your emails.`);
        }
      );
  }

  public sendVerificationEmail() :void {
    this.dialogService.openConfirmDialog($localize `:@@action.user.send_validation_email:Send validation email`, this.confirmSendVerificationEmail.bind(this));
  }

  public editUser(user: User) :void {
    this.modalService.openUserEditModal(user, (data: ComponentInputDomainData) => {this.user$ = this.userService.getUser();});
  }
}