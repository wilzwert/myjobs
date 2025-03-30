import { Injectable } from '@angular/core';
import { RegistrationRequest } from '../model/registration-request.interface';
import { Observable, switchMap } from 'rxjs';
import { DataService } from './data.service';
import { LoginRequest } from '../model/login-request.interface';
import { SessionInformation } from '../model/session-information.interface';
import { HttpHeaders, HttpParams } from '@angular/common/http';
import { CaptchaService } from './captcha.service';
import { ResetPasswordRequest } from '../model/reset-password-request.interface';
import { NewPasswordRequest } from '../model/new-password-request.interface';
import { ValidateEmailRequest } from '../model/validate-email-request.interface';

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

  public validateEmail(validateEmailRequest: ValidateEmailRequest): Observable<void> {
    console.log(validateEmailRequest);
    /*return this.captchaService.getCaptchaToken().pipe(
      switchMap(() => {*/
        return this.dataService.post<void>(`${this.apiPath}/email/validation`, validateEmailRequest);
      /*})
    );*/
  }

}
