import { Injectable, ɵDeferBlockConfig } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormControl, ValidatorFn } from '@angular/forms';
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

  checkEmailExistsAsync(value: string | null = null): AsyncValidatorFn {
    return (control:  AbstractControl<any, any>): Observable<{ emailExists: boolean } | null>  => {
      return timer(500).pipe(
          switchMap(() => {
            if(value !== null && control.value === value) {
              return of(true);
            }
            return this.checkExists('email', control.value);
          }),
          map(() => null),
          catchError(() => of({emailExists: true}))
      );
    } 
  }
  /*
  checkEmailExistsAsyncOLD(control: FormControl): Observable<{ emailExists: boolean } | null> {
    return timer(500).pipe(
        switchMap(() => this.checkExists('email', control.value)),
        map(() => null),
        catchError(() => of({emailExists: true}))
    );
  }*/

  checkUsernameExistsAsync(value: string | null = null): AsyncValidatorFn {
    return (control:  AbstractControl<any, any>): Observable<{ usernameExists: boolean } | null>  => {
      return timer(500).pipe(
          switchMap(() => {
            if(value !== null && control.value === value) {
              return of(true);
            }
            return this.checkExists('username', control.value);
          }),
          map(() => null),
          catchError(() => of({usernameExists: true}))
      );
    } 
  }
    

  /*

  checkUsernameExistsAsync(control: FormControl): Observable<{ usernameExists: boolean } | null> {
    return timer(500).pipe(
        switchMap(() => this.checkExists('username', control.value)),
        map(() => null),
        catchError(() => of({usernameExists: true}))
    );
  }*/
}
