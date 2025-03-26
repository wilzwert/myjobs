import { HttpErrorResponse, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { SessionService } from '../services/session.service';
import { AuthService } from "../services/auth.service";
import { BehaviorSubject, catchError, filter, switchMap, take, throwError } from "rxjs";
import { SessionInformation } from "../model/session-information.interface";


@Injectable({ providedIn: 'root' })
export class JwtInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

  constructor(private sessionService: SessionService, private authService: AuthService) {}

  public intercept(request: HttpRequest<unknown>, next: HttpHandler) {
    if (!this.sessionService.isLogged()) {
      return next.handle(request);
    }

    if(!request.url.match(/api\//)) {
      return next.handle(request);
    }

    return next.handle(request).pipe(
      catchError(error => {
        if (this.shouldTryToRefreshToken(request, error)) {
          return this.tryToRefreshToken(request, next);
        }

        return throwError(() => error);
      })
    );
  }

  private shouldTryToRefreshToken(request: HttpRequest<unknown>, error: HttpErrorResponse) :boolean {
    return error instanceof HttpErrorResponse && error.status === 401 && !request.url.includes('auth/login') && !request.url.includes('auth/refreshToken')
  }

  private tryToRefreshToken(request: HttpRequest<unknown>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(false);

      const isLogged = this.sessionService.isLogged();

      if (isLogged) {
        return this.authService.refreshToken().pipe(
          switchMap((data: SessionInformation) => {
            this.isRefreshing = false;
            this.sessionService.logIn(data);
            this.refreshTokenSubject.next(true);
            return next.handle(request);
          }),
          catchError((error) => {
            this.isRefreshing = false;
            this.refreshTokenSubject.next(false);
            this.sessionService.logOut();
            return throwError(() => error);
          })
        );
      }
    }

    // let's handle original request with the new token
    return this.refreshTokenSubject.pipe(
      filter(token => token !== false),
      take(1),
      switchMap(() => next.handle(request))
    );
  }
}
