import { ErrorHandler, Injectable } from '@angular/core';
import { NotificationService } from './notification.service';

/**
 * Overrides the default error handler
 * Useful to intercept all errors and pass them to the NotificationService
 */

@Injectable()
export class GlobalErrorHandler extends ErrorHandler {

    constructor(private notificationService: NotificationService) {
        super();
    }


    override handleError(error: Error) {
        console.error('GlobalErrorHandler caught an error: ', error);
        this.notificationService.error(error.message??'', error);
    }
}