import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../core/services/notification.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AppNotification } from '../../core/model/app-notification.interface';
import { ApiError } from '../../core/errors/api-error';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [],
  templateUrl: './notification.component.html',
  styleUrl: './notification.component.scss'
})
export class NotificationComponent implements OnInit {
  constructor(private notificationService: NotificationService, private matSnackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.notificationService.notification$.subscribe((notification: AppNotification | null) => {
        if(notification != null) {
          // display snack bar only if error is not 401 returned from the API
          // when a 401 is returned from the API the notification MUST be handled by a session service or a auth-related component
          if(notification.error == null || !(notification.error instanceof ApiError) || notification.error.httpStatus !== 401) {
            this.matSnackBar.open(notification.message, 'Close', {duration: 3000});
          }
        }
    });
  }
}
