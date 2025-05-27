import { Injectable } from '@angular/core';
import { RegistrationRequest } from '@core/model/registration-request.interface';
import { Observable, switchMap } from 'rxjs';
import { DataService } from './data.service';
import { LoginRequest } from '@core/model/login-request.interface';
import { SessionInformation } from '@core/model/session-information.interface';
import { HttpHeaders } from '@angular/common/http';
import { CaptchaService } from './captcha.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiPath = 'auth';

  constructor(private dataService: DataService, private captchaService: CaptchaService) { }

  public register(registrationRequest: RegistrationRequest): Observable<null> {
    return this.captchaService.getCaptchaToken().pipe(
      switchMap((token: string) => {
        return this.dataService.post<null>(`${this.apiPath}/register`, registrationRequest, {headers: new HttpHeaders().set('Captcha-Response', token)});
      })
    );
  }

  public login(loginRequest: LoginRequest): Observable<SessionInformation> {
    return this.captchaService.getCaptchaToken().pipe(
      switchMap((token: string) => {
        return this.dataService.post<SessionInformation>(`${this.apiPath}/login`, loginRequest, {headers: new HttpHeaders().set('Captcha-Response', token)});
      })
    );
  }

  public logout(): Observable<void> {
    return this.dataService.post<void>(`${this.apiPath}/logout`, null);
  }
  
  public refreshToken(): Observable<SessionInformation> {
    return this.dataService.post<SessionInformation>(`${this.apiPath}/refresh-token`, null);
  }

}
