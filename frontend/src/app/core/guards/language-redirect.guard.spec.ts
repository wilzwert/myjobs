import { TestBed } from '@angular/core/testing';

import { LanguageRedirectGuard } from './language-redirect.guard';

describe('LanguageRedirectGuard', () => {
  let guard: LanguageRedirectGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(LanguageRedirectGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
