import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, from, of, switchMap, tap, map } from 'rxjs';
import { solveChallenge, type Challenge, type Solution } from 'altcha/lib';
import { pbkdf2 } from 'altcha/lib';


@Injectable({
  providedIn: 'root'
})
export class CaptchaService {

  private readonly challengeUrl = '/api/altcha/challenge';
  private tokenSubject = new BehaviorSubject<string | null | false>(null);

  constructor(private http: HttpClient) {}

  getCaptchaToken(): Observable<string> {
    console.log('Getting captcha token...');
    return this.http.get<Challenge>(this.challengeUrl).pipe(
      switchMap(challenge =>
        from(solveChallenge({ challenge, deriveKey: pbkdf2.deriveKey })).pipe(
          map((solution: Solution | null) => {
            console.log('Captcha challenge solved:', solution);
            if (!solution) {
              throw new Error('Unable to solve captcha challenge');
            }
            return this.encodeToken(challenge, solution);
          })
        )
      ),
      tap(token => this.tokenSubject.next(token))
    );
  }

  private encodeToken(challenge: Challenge, solution: Solution): string {
    return btoa(JSON.stringify({ challenge, solution }));
  }
}