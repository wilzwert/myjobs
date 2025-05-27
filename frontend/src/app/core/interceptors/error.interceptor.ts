import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { catchError, Observable } from 'rxjs';
import { ApiError } from '@core/errors/api-error';
import { ErrorProcessorService } from '@core/services/error-processor.service';

/**
 * Intercepts http errors to rethrow structured ApiError
 */

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private errorProcessorService: ErrorProcessorService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request)
      .pipe(
        // at this point, errors can be "native" HttpErrorResponse or ApiError already created by this ErrorInterceptor
        catchError((error: HttpErrorResponse | ApiError) => {
          // Handle the error
          if(error instanceof HttpErrorResponse) {
            return this.errorProcessorService.processError(new ApiError(error));
          }
          return this.errorProcessorService.processError(error);
        })
      );
  }
}
