import { Component, OnInit } from '@angular/core';
import { NotificationService } from '@core/services/notification.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AppNotification } from '@core/model/app-notification.interface';
import { ApiError } from '@core/errors/api-error';
import { TranslatorService } from '@core/services/translator.service';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [],
  templateUrl: './notification.component.html',
  styleUrl: './notification.component.scss'
})
export class NotificationComponent implements OnInit {
  constructor(private notificationService: NotificationService, private matSnackBar: MatSnackBar, private translatorService: TranslatorService) {}

  ngOnInit(): void {
    this.notificationService.notification$.subscribe((notification: AppNotification | null) => {
        if(notification != null) {
          // in case notification is a confirmation or an information, just display it
          if(notification.type != 'error') {
            this.matSnackBar.open(notification.message, $localize `:@@action.close:Close`, {duration: 3000});
          }
          else {
            // display snack bar only if error is not 401 returned from the API
            // when a 401 is returned from the API the notification MUST be handled by a session service or a auth-related component
            // in case of an error we call the translator service to handle backend error codes
            if(notification.error == null || !(notification.error instanceof ApiError) || notification.error.httpStatus !== 401) {
              // only ApiError messages are translatable by the TranslatorService
              this.matSnackBar.open((notification.error instanceof ApiError ? this.translatorService.translateError(notification.message) : notification.message), $localize `:@@action.close:Close`, {duration: 3000});
            }
          }
        }
    });
  }
}