import { Injectable } from '@angular/core';
import { SessionInformation } from '../model/session-information.interface';
import { RefreshTokenResponse } from '../model/refresh-token-response.interface';
import { BehaviorSubject } from 'rxjs';

const SESSION_INFO_KEY = 'session-info';

@Injectable({
  providedIn: 'root'
})
export class SessionStorageService {

  private sessionInfo: SessionInformation|null = null;
  private sessionInfoSubject = new BehaviorSubject<SessionInformation|null>(null);

  constructor() { 
    let rawInfo = window.localStorage.getItem(SESSION_INFO_KEY);
    if(rawInfo !== null) {
      this.sessionInfo = JSON.parse(rawInfo);
    }
    this.sessionInfoSubject.next(this.sessionInfo);
  }

  public clearSessionInformation() :void {
    window.localStorage.removeItem(SESSION_INFO_KEY);
    this.sessionInfo = null;
  }

  public getSessionInformation() :SessionInformation | null {
    return this.sessionInfo;
  }

  public $getSessionInformation(): BehaviorSubject<SessionInformation|null> {
    return this.sessionInfoSubject;
  }

  public saveSessionInformation(data : SessionInformation) :void {
    this.sessionInfo = data;
    window.localStorage.setItem(SESSION_INFO_KEY, JSON.stringify(data));
    this.sessionInfoSubject.next(this.sessionInfo);
  }

  // TODO ?
  public saveTokenAfterRefresh(data: RefreshTokenResponse): void {
    // this.clearSessionInformation();
  }
}