import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { ApiError } from '../errors/api-error';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request)
      .pipe(
        // at this point, errors can be "native" HttpErrorResponse or ApiError already created by this ErrorInterceptor
        catchError((error: HttpErrorResponse | ApiError) => {
          // Handle the error
          if(error instanceof HttpErrorResponse) {
            return throwError(() => new ApiError(error));
          }
          return throwError(() => error);
          
        })
      );
  }
}
