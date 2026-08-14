import { TestBed } from '@angular/core/testing';

import { environment } from '@environments/environment';
import { LanguageRedirectGuard } from './language-redirect.guard';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('LanguageRedirectGuard', () => {
  let guard: LanguageRedirectGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
      ]
    });
    guard = TestBed.inject(LanguageRedirectGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
