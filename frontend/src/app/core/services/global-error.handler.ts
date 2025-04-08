import { ErrorHandler, Injectable } from '@angular/core';
import { NotificationService } from './notification.service';
import { ApiError } from '../errors/api-error';


@Injectable()
export class GlobalErrorHandler extends ErrorHandler {

    constructor(private noticationService: NotificationService) {
        super();
    }


    override handleError(error: Error) {
        // Custom error handling logic
        if(error instanceof ApiError) {
            console.log(error);
        }
        
        this.noticationService.error(error.message??'', error);
        // TODO : should the error be thrown again ?
        // throw error;
    }
}