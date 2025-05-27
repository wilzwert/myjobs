import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';


/**
 * This service externalizes the throwError() call
 * Useful e.g. when components have to rethrow an error in a catchError() + subscribe() context
 * It improves testabiliy to use an external service instead of a direct throwError call
 * because errors contents may be tested by spying on the processError method
 * Otherwise, direct throwError calls cannot be checked as they are never passed to the GlobalErrorHandler while testing
 * 
 */
@Injectable({
  providedIn: 'root'
})
export class ErrorProcessorService {

  public processError(error: Error): Observable<never> {
    return throwError(() => error);
  }
}
