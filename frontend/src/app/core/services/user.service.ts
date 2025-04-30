import { Injectable } from '@angular/core';
import { map, Observable, switchMap } from 'rxjs';
import { DataService } from './data.service';
import { CaptchaService } from './captcha.service';
import { ResetPasswordRequest } from '../model/reset-password-request.interface';
import { NewPasswordRequest } from '../model/new-password-request.interface';
import { ValidateEmailRequest } from '../model/validate-email-request.interface';
import { User } from '../model/user.interface';
import { ChangePasswordRequest } from '../model/change-password-request.interface';
import { EmailStatus } from '../model/email-status';
import { EditUserRequest } from '../model/edit-user-request.interface';
import { EditUserLangRequest } from '../model/edit-user-lang-request.interface';
import { Lang } from '../model/lang';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiPath = 'user/me';

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
    return this.dataService.put<void>(`${this.apiPath}/password`, changePasswordRequest);
  }

  public sendVerificationMail(): Observable<void> {
    return this.dataService.post<void>(`${this.apiPath}/email/verification`, null);
  }

  public validateEmail(validateEmailRequest: ValidateEmailRequest): Observable<void> {
    /*return this.captchaService.getCaptchaToken().pipe(
      switchMap(() => {*/
        return this.dataService.post<void>(`${this.apiPath}/email/validation`, validateEmailRequest);
      /*})
    );*/
  }

  public getUser(): Observable<User> {
    /*return this.captchaService.getCaptchaToken().pipe(
      switchMap(() => {*/
        return this.dataService.get<User>(`${this.apiPath}`);
        return this.dataService.get<User>(`${this.apiPath}`).pipe(map((user: User) => {user.emailStatus = user.emailStatus as EmailStatus; return user;}));
      /*})
    );*/
  }

  public deleteUser() :Observable<void> {
    return this.dataService.delete<void>(`${this.apiPath}`);
  }

  public editUser(editUserRequest: EditUserRequest) :Observable<User> {
    return this.dataService.patch<User>(`${this.apiPath}`, editUserRequest);
  }

  public saveUserLang(newLang: string) :Observable<void> {
    return this.dataService.put<void>(`${this.apiPath}/lang`, {lang: newLang.toUpperCase()} as EditUserLangRequest);
  }
}
