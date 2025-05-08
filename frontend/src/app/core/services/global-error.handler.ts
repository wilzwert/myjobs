import { ErrorHandler, Injectable } from '@angular/core';
import { NotificationService } from './notification.service';
import { ApiError } from '../errors/api-error';


@Injectable()
export class GlobalErrorHandler extends ErrorHandler {

    constructor(private notificationService: NotificationService) {
        super();
    }


    override handleError(error: Error) {
        // Custom error handling logic
        if(error instanceof ApiError) {
            console.log(error);
        }
        
        // TODO : implement translation
        this.notificationService.error(error.message??'', error);
    }
}