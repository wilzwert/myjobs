import { Injectable } from '@angular/core';
import { Observable, switchMap } from 'rxjs';
import { DataService } from './data.service';
import { CaptchaService } from './captcha.service';
import { ResetPasswordRequest } from '@core/model/reset-password-request.interface';
import { NewPasswordRequest } from '@core/model/new-password-request.interface';
import { ValidateEmailRequest } from '@core/model/validate-email-request.interface';
import { User } from '@core/model/user.interface';
import { ChangePasswordRequest } from '@core/model/change-password-request.interface';
import { EditUserRequest } from '@core/model/edit-user-request.interface';
import { EditUserLangRequest } from '@core/model/edit-user-lang-request.interface';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiPath = 'user';

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
