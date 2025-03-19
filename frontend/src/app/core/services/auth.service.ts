import { Injectable } from '@angular/core';
import { RegistrationRequest } from '../model/registration-request.interface';
import { Observable } from 'rxjs';
import { DataService } from './data.service';
import { LoginRequest } from '../model/login-request.interface';
import { SessionInformation } from '../model/session-information.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiPath = 'auth';

  constructor(private dataService: DataService) { }

  public register(registrationRequest: RegistrationRequest): Observable<null> {
    return this.dataService.post<null>(`${this.apiPath}/register`, registrationRequest);
  }

  public login(loginRequest: LoginRequest): Observable<SessionInformation> {
    return this.dataService.post<SessionInformation>(`${this.apiPath}/login`, loginRequest);
  }
  /* TODO
  public refreshToken(refreshTokenRequest: RefreshTokenRequest): Observable<RefreshTokenResponse> {
    return this.dataService.post<RefreshTokenResponse>(`${this.apiPath}/refreshToken`, refreshTokenRequest);
  }*/

}
