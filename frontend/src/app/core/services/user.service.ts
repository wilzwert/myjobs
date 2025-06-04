import { Injectable, signal, Signal, WritableSignal } from '@angular/core';
import { map, Observable, switchMap } from 'rxjs';
import { DataService } from './data.service';
import { CaptchaService } from './captcha.service';
import { ResetPasswordRequest } from '@core/model/reset-password-request.interface';
import { NewPasswordRequest } from '@core/model/new-password-request.interface';
import { ValidateEmailRequest } from '@core/model/validate-email-request.interface';
import { User } from '@core/model/user.interface';
import { ChangePasswordRequest } from '@core/model/change-password-request.interface';
import { EditUserRequest } from '@core/model/edit-user-request.interface';
import { EditUserLangRequest } from '@core/model/edit-user-lang-request.interface';
import { UserSummary } from '../model/user-summary.interface';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiPath = 'user';

  private userSummary: WritableSignal<UserSummary | null> = signal(null); 

  private userSummaryLoaded: boolean = false;

  constructor(private dataService: DataService, private captchaService: CaptchaService) { }

  public resetPassword(resetPasswordRequest: ResetPasswordRequest): Observable<void> {
    return this.captchaService.getCaptchaToken().pipe(
      switchMap(() => {
        return this.dataService.post<void>(`${this.apiPath}/password/reset`, resetPasswordRequest);
      })
    );
  }

  public newPassword(newPasswordRequest: NewPasswordRequest): Observable<void> {
    return this.captchaService.getCaptchaToken().pipe(
      switchMap(() => {
        return this.dataService.post<void>(`${this.apiPath}/password`, newPasswordRequest);
      })
    );
  }

  public changePassword(changePasswordRequest: ChangePasswordRequest): Observable<void> {
    return this.dataService.put<void>(`${this.apiPath}/me/password`, changePasswordRequest);
  }

  public sendVerificationMail(): Observable<void> {
    return this.dataService.post<void>(`${this.apiPath}/me/email/verification`, null);
  }

  public validateEmail(validateEmailRequest: ValidateEmailRequest): Observable<void> {
    /*return this.captchaService.getCaptchaToken().pipe(
      switchMap(() => {*/
        return this.dataService.post<void>(`${this.apiPath}/me/email/validation`, validateEmailRequest);
      /*})
    );*/
  }

  public getUser(): Observable<User> {
    /*return this.captchaService.getCaptchaToken().pipe(
      switchMap(() => {*/
        return this.dataService.get<User>(`${this.apiPath}/me`);
        // return this.dataService.get<User>(`${this.apiPath}`).pipe(map((user: User) => {user.emailStatus = user.emailStatus as EmailStatus; return user;}));
      /*})
    );*/
  }


  /** Méthode à appeler pour recharger manuellement */
  public async reloadUserSummary(): Promise<void> {
    await this.loadUserSummary();
  }


   private async loadUserSummary(): Promise<void> {
    console.log('getting getUserSummary');
    this.userSummaryLoaded = true;
    /*return this.captchaService.getCaptchaToken().pipe(
      switchMap(() => {*/
        this.dataService.get<UserSummary>(`${this.apiPath}/me/summary`)
        .subscribe(summary => {
          console.log('setting UserSummary signal');
          this.userSummary.set(summary);
        })
          /*.pipe(
            map(s => {
              s.jobStatuses = new Map(Object.entries(s.jobStatuses));
              return s;
            })
          )*/;
        // return this.dataService.get<User>(`${this.apiPath}`).pipe(map((user: User) => {user.emailStatus = user.emailStatus as EmailStatus; return user;}));
      /*})
    )*/;
  }

  

  public getUserSummary(): Signal<UserSummary | null> {
    if(!this.userSummaryLoaded) {
      // force first load if necessary
      this.loadUserSummary();
    }
    return this.userSummary.asReadonly();
  }

  public deleteUser() :Observable<void> {
    return this.dataService.delete<void>(`${this.apiPath}/me`);
  }

  public editUser(editUserRequest: EditUserRequest) :Observable<User> {
    return this.dataService.patch<User>(`${this.apiPath}/me`, editUserRequest);
  }

  public saveUserLang(newLang: string) :Observable<void> {
    return this.dataService.put<void>(`${this.apiPath}/me/lang`, {lang: newLang.toUpperCase()} as EditUserLangRequest);
  }
}
