import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { SessionStorageService } from './token-storage.service';
import { Router } from '@angular/router';
import { SessionInformation } from '../model/session-information.interface';
import { RefreshTokenResponse } from '../model/refresh-token-response.interface';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  public logged = false;

  private isLoggedSubject = new BehaviorSubject<boolean>(this.logged);

  constructor(
    private sessionStorageService: SessionStorageService, 
    private router: Router) {
    if(this.sessionStorageService.getSessionInformation() != null) {
      this.logged = true;
      this.next();
    }
  }

  public isLogged() :boolean {
    return this.logged;
  }
  /*
  public getToken() :string | null {
    return this.sessionStorageService.getToken();
  }

  public getTokenType() :string | null {
    return this.sessionStorageService.getTokenType();
  }

  public getRefreshToken() :string | null {
    return this.sessionStorageService.getRefreshToken();
  }*/

  public $isLogged(): Observable<boolean> {
    return this.isLoggedSubject.asObservable();
  }

  public $getSessionInformation() :BehaviorSubject<SessionInformation|null> {
    return this.sessionStorageService.$getSessionInformation();
  }

  public handleTokenAfterRefresh(data: RefreshTokenResponse): void {
    this.sessionStorageService.saveTokenAfterRefresh(data);
  }

  public logIn(data: SessionInformation): void {
    this.sessionStorageService.saveSessionInformation(data);
    this.logged = true;
    this.next();
  }

  public logOut(): void {
    // clear user and session related data from storage
    this.sessionStorageService.clearSessionInformation();
    this.logged = false;
    this.next();
    this.router.navigate(['']);
  }

  private next(): void {
    this.isLoggedSubject.next(this.logged);
  }
}
