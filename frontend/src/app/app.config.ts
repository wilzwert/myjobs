import { ApplicationConfig, ErrorHandler, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withDebugTracing, withEnabledBlockingInitialNavigation } from '@angular/router';
import { environment } from '@environments/environment';

import { routes } from './app.routes';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import { provideScReCaptchaSettings } from '@semantic-components/re-captcha';
import { GlobalErrorHandler } from './core/services/global-error.handler';
import { ErrorInterceptor } from './core/interceptors/error.interceptor';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes, withEnabledBlockingInitialNavigation(), withDebugTracing()), 
    provideHttpClient(withInterceptorsFromDi()),
    /*provideAnimations(),*/
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline', subscriptSizing: 'dynamic'}},
    provideScReCaptchaSettings({
      v3SiteKey: environment.recaptcha_key,
      languageCode: 'fr',
    })
  ]
};