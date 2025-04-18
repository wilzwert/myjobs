import { Injectable } from '@angular/core';
import { ScScoreReCaptcha } from '@semantic-components/re-captcha';
import { BehaviorSubject, from, Observable, of, switchMap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CaptchaService {

  private tokenSubject: BehaviorSubject<string | null | false> = new BehaviorSubject<string | null | false>(null);

  constructor(private scScoreReCaptcha: ScScoreReCaptcha) {}

  getCaptchaToken() :Observable<string> {
    return from(this.scScoreReCaptcha.execute('captcha')).pipe(
      switchMap((token: string) =>  {
          this.tokenSubject.next(token);
          return of(token);
      }
    ));
  }
}
