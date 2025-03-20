import { Injectable, ɵDeferBlockConfig } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormControl, ValidationErrors } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { map, catchError, switchMap, mergeMap } from 'rxjs/operators';
import { BehaviorSubject, from, Observable, of, timer } from 'rxjs';
import { DataService } from './data.service';
import { ScScoreReCaptcha } from '@semantic-components/re-captcha';
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

  checkEmailExistsAsync(control: FormControl): Observable<{ emailExists: boolean } | null> {
    return timer(500).pipe(
        switchMap(() => this.checkExists('email', control.value)),
        map(() => null),
        catchError(() => of({emailExists: true}))
    );
  }


  checkUsernameExistsAsync(control: FormControl): Observable<{ usernameExists: boolean } | null> {
    return timer(500).pipe(
        switchMap(() => this.checkExists('username', control.value)),
        map(() => null),
        catchError(() => of({usernameExists: true}))
    );
  }
}
