import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { SessionStorageService } from './session-storage.service';
import { Router } from '@angular/router';
import { SessionInformation } from '@core/model/session-information.interface';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  public logged = false;

  private isLoggedSubject = new BehaviorSubject<boolean>(this.logged);

  constructor(
    private sessionStorageService: SessionStorageService, 
    private router: Router,
    private notificationService: NotificationService) {
    if(this.sessionStorageService.getSessionInformation() != null) {
      this.logged = true;
      this.next();
    }
  }

  public isLogged() :boolean {
    return this.logged;
  }
  
  public $isLogged(): Observable<boolean> {
    return this.isLoggedSubject.asObservable();
  }

  public $getSessionInformation(): BehaviorSubject<SessionInformation|null> {
    return this.sessionStorageService.$getSessionInformation();
  }

  public logIn(data: SessionInformation): void {
    this.sessionStorageService.saveSessionInformation(data);
    this.logged = true;
    this.next();
  }

  public logOut(redirect: boolean = true): void {
    // clear user and session related data from storage
    this.sessionStorageService.clearSessionInformation();
    this.logged = false;
    this.next();
    if(redirect) {
      this.router.navigate(['']);
    }
    this.notificationService.information($localize `:@@message.disconnected:You have been disconnected.`);
  }

  private next(): void {
    this.isLoggedSubject.next(this.logged);
  }
}
