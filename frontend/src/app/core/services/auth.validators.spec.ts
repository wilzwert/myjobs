import { TestBed } from '@angular/core/testing';
import { AuthValidators } from './auth.validators';
import { AuthService } from '../services/auth.service';
import { firstValueFrom, of, throwError } from 'rxjs';
import { FormControl } from '@angular/forms';

describe('AuthValidators', () => {
  let authValidators: AuthValidators;
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let authService: AuthService;

  beforeEach(() => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['checkEmail', 'checkUsername']);
    
    TestBed.configureTestingModule({
      providers: [
        AuthValidators,
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });

    authValidators = TestBed.inject(AuthValidators);
    authServiceMock = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    authService = TestBed.inject(AuthService);
  });

  it('should return null if email is available', async (done) => {
    authServiceMock.checkEmail.and.returnValue(of());

    const validator = authValidators.emailExists();
    const control = new FormControl('test@example.com');

     // Simuler la réponse de l'API avec un observable
     spyOn(authService, 'checkUsername').and.returnValue(of()); 

    const result = await firstValueFrom(validator(control)); // Attendre que l'observable soit résolu
    
    !.pipe().subscribe(result => {
      expect(result).toBeNull();
      done();
    });
  });

  it('should return { emailExists: true } if email is taken', (done) => {
    authServiceMock.checkEmail.and.returnValue(throwError(() => new Error('Email already exists')));

    authValidators.emailExists()(new FormControl('test@example.com')).subscribe(result => {
      expect(result).toEqual({ emailExists: true });
      done();
    });
  });
});
