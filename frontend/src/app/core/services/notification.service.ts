import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { AppNotification } from '@core/model/app-notification.interface';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private notificationSubject$ = new Subject<AppNotification | null>();
  public notification$: Observable<AppNotification | null> = this.notificationSubject$.asObservable();

  public error(errorMessage: string, error: Error | null) :void {
    this.handleNotification({type: 'error', message: errorMessage, error} as AppNotification);
  }

  public confirmation(confirmationMessage: string) :void {
    this.handleNotification({type: 'confirmation', message: confirmationMessage} as AppNotification);
  }

  public information(informationMessage: string) :void {
    this.handleNotification({type: 'information', message: informationMessage} as AppNotification);
  }

  private handleNotification(notification: AppNotification) :void {
    this.notificationSubject$.next(notification);
  }

  reset(): void {
    this.notificationSubject$.next(null);
  }
}
