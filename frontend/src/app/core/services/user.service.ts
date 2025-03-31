import { Injectable } from '@angular/core';
import { Observable, switchMap } from 'rxjs';
import { DataService } from './data.service';
import { CaptchaService } from './captcha.service';
import { ResetPasswordRequest } from '../model/reset-password-request.interface';
import { NewPasswordRequest } from '../model/new-password-request.interface';
import { ValidateEmailRequest } from '../model/validate-email-request.interface';
import { User } from '../model/user.interface';
import { ChangePasswordRequest } from '../model/change-password-request.interface';

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
    return this.dataService.put<void>(`${this.apiPath}/password`, changePasswordRequest);
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
        return this.dataService.get<User>(`${this.apiPath}/me`);
      /*})
    );*/
  }

  public deleteUser() :Observable<void> {
    return this.dataService.delete<void>(`${this.apiPath}`);
  }
}
