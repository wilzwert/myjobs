import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import localeEn from '@angular/common/locales/en';

registerLocaleData(localeFr, 'fr');
registerLocaleData(localeEn, 'en');

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
