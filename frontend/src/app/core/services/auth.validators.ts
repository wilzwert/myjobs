import { Injectable } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { map, catchError, switchMap } from 'rxjs/operators';
import { Observable, of, timer } from 'rxjs';
import { DataService } from './data.service';
import { CaptchaService } from './captcha.service';
import { HttpHeaders } from '@angular/common/http';


@Injectable({ providedIn: 'root' })
export class AuthValidators {


  constructor(private dataService: DataService, private captchaService: CaptchaService) {}

  checkExists(field: string, search: string): Observable<boolean> {
    return this.captchaService.getCaptchaToken().pipe(
        switchMap((token: string) => {
            return this.dataService.get<boolean>(`auth/${field}-check?${field}=${search}`, {headers: new HttpHeaders().set('Captcha-Response', token)});
        })
    );
  }
  
//  checkEmailExistsAsync(originalValue: string | null = null): AsyncValidatorFn {
  checkEmailExistsAsync(originalValue: string | null = null): (control: AbstractControl) => Observable<ValidationErrors | null> {
    return (control:  AbstractControl<any, any>): Observable<{ emailExists: boolean } | null>  => {
      return timer(500).pipe(
          switchMap(() => {
            // orignal value was set and has not changed : nothing to do
            if(originalValue !== null && control.value === originalValue) {
              return of(true);
            }
            // actual check
            return this.checkExists('email', control.value);
          }),
          // by default, a successful request (i.e. 200) implies the email is available
          map(() => null),
          // backend generates an http response with an error to inform email exists 
          catchError(() => of({emailExists: true}))
      );
    } 
  }
  
  checkUsernameExistsAsync(originalValue: string | null = null): (control: AbstractControl) => Observable<ValidationErrors | null> {
    return (control:  AbstractControl<any, any>): Observable<{ usernameExists: boolean } | null>  => {
      return timer(500).pipe(
          switchMap(() => {
            // orignal value was set and has not changed : nothing to do
            if(originalValue !== null && control.value === originalValue) {
              return of(true);
            }
            // actual check
            return this.checkExists('username', control.value);
          }),
          map(() => null),
          // backend generates an http response with an error to inform username exists 
          catchError(() => of({usernameExists: true}))
      );
    } 
  }
}