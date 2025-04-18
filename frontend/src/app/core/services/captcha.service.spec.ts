import { TestBed } from '@angular/core/testing';

import { CaptchaService } from './captcha.service';
import { ScScoreReCaptcha } from '@semantic-components/re-captcha';

describe('CaptchaService', () => {
  let captchaService: CaptchaService;
  let scScoreReCaptchaMock: jest.Mocked<ScScoreReCaptcha>;

  beforeEach(() => {
    scScoreReCaptchaMock = {
      execute: jest.fn()
    } as unknown as jest.Mocked<ScScoreReCaptcha>;

    captchaService = new CaptchaService(scScoreReCaptchaMock);
  });

  afterEach(() => {
    jest.resetAllMocks();
  });

  it('should be created', () => {
    expect(captchaService).toBeTruthy();
  });

  it('should return captcha token', (done) => {
    const spy = jest.spyOn(scScoreReCaptchaMock, 'execute').mockResolvedValue('captcha-token');

    captchaService.getCaptchaToken().subscribe({
      next: (token) => {
        expect(token).toEqual('captcha-token');
        expect(spy).toHaveBeenCalledTimes(1);
        done();
      },
      error: () => fail('No error should have been thrown')
    })

  });
});
