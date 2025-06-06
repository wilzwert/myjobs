import { Injectable } from '@angular/core';
import { SessionInformation } from '@core/model/session-information.interface';
import { BehaviorSubject } from 'rxjs';
import { DataStorageService } from './data-storage.service';

const SESSION_INFO_KEY = 'session-info';

@Injectable({
  providedIn: 'root'
})
export class SessionStorageService {

  private sessionInfo: SessionInformation|null = null;
  private readonly sessionInfoSubject = new BehaviorSubject<SessionInformation|null>(null);

  constructor(private readonly dataStorageService: DataStorageService) { 
    this.sessionInfo = this.dataStorageService.getItem<SessionInformation>(SESSION_INFO_KEY);
    this.sessionInfoSubject.next(this.sessionInfo);
  }

  public clearSessionInformation() :void {
    this.dataStorageService.removeItem(SESSION_INFO_KEY);
    this.sessionInfo = null;
    this.sessionInfoSubject.next(null);
  }

  public getSessionInformation() :SessionInformation | null {
    return this.sessionInfo;
  }

  public $getSessionInformation(): BehaviorSubject<SessionInformation|null> {
    return this.sessionInfoSubject;
  }

  public saveSessionInformation(data : SessionInformation) :void {
    this.sessionInfo = data;
    this.dataStorageService.setItem(SESSION_INFO_KEY, data);
    this.sessionInfoSubject.next(this.sessionInfo);
  }
}