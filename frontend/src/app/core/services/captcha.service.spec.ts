import { CaptchaService } from './captcha.service';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import * as altchaLib from 'altcha/lib';

jest.mock('altcha/lib', () => ({
  solveChallenge: jest.fn(),
  pbkdf2: {
    deriveKey: jest.fn()
  }
}));

describe('CaptchaService', () => {
  let captchaService: CaptchaService;
  let httpClientMock: jest.Mocked<HttpClient>;

  const mockChallenge = {
    parameters: {
      algorithm: 'PBKDF2/SHA-256',
      nonce: 'b5f8667d01249d73dc576f1b0f293146',
      salt: '66752c8f14059b8ff0eb596f1474850d',
      cost: 5000,
      keyLength: 32,
      keyPrefix: '00',
      keySignature: null,
      memoryCost: null,
      parallelism: null,
      expiresAt: 1786718112,
      data: null
    },
    signature: 'fce07e475acbe377b2b8dc41461db0ba2da70b7941baacea9efe158f2c48e99b'
  };

  beforeEach(() => {
    httpClientMock = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      patch: jest.fn(),
      delete: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;
    captchaService = new CaptchaService(httpClientMock);
  });

  afterEach(() => {
    jest.resetAllMocks();
  });

  it('should be created', () => {
    expect(captchaService).toBeTruthy();
  });

  it('should return captcha token when challenge is solved', (done) => {
    httpClientMock.get.mockReturnValue(of(mockChallenge as any));

    const mockSolution = { counter: 106, derivedKey: '006a81febe448d77a6c916b3359fc784524fd0c7eb7f72426f10a731e6f37922', time: 42 };
    const solveChallengeSpy = jest
      .spyOn(altchaLib, 'solveChallenge')
      .mockResolvedValue(mockSolution as any);

    captchaService.getCaptchaToken().subscribe({
      next: (token) => {
        expect(httpClientMock.get).toHaveBeenCalledWith('/api/altcha/challenge');
        expect(solveChallengeSpy).toHaveBeenCalledTimes(1);
        expect(solveChallengeSpy).toHaveBeenCalledWith(
          expect.objectContaining({
            challenge: mockChallenge,
            deriveKey: altchaLib.pbkdf2.deriveKey
          })
        );

        const decoded = JSON.parse(atob(token));
        expect(decoded).toEqual({
          challenge: mockChallenge,
          solution: mockSolution
        });
        done();
      },
      error: () => fail('No error should have been thrown')
    });
  });

  it('should throw an error when the challenge cannot be solved', (done) => {
    httpClientMock.get.mockReturnValue(of(mockChallenge as any));

    jest.spyOn(altchaLib, 'solveChallenge').mockResolvedValue(null);

    captchaService.getCaptchaToken().subscribe({
      next: () => fail('No token should have been emitted'),
      error: (err) => {
        expect(err.message).toEqual('Unable to solve captcha challenge');
        done();
      }
    });
  });
});