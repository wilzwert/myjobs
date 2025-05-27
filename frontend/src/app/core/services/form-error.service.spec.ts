import { FormControl, FormGroup, ValidationErrors } from '@angular/forms';
import { FormErrorService } from './form-error.service';

describe('FormErrorService', () => {
  let service: FormErrorService;

  beforeEach(() => {
    const translatorMock = {
      translateError: jest.fn((code: string, details: Record<string, string> | null = {}) => {
        let result: string = `translated_${code}`;
        result += ` translated_${details!['details']}`
        return result;
      })
    };
    service = new FormErrorService(translatorMock as any);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should set backend errors and clean them on value change', () => {
    const form = new FormGroup({
      email: new FormControl('old@example.com'),
      username: new FormControl('oldUser')
    });

    const errors = {
      'email': [{code: 'EMAIL_ALREADY_TAKEN', details: {'details': 'Email already taken'}}],
      'username': [{code: 'USERNAME_INVALID', details: {'details': 'Username is invalid'}}]
    };

    service.setBackendErrors(form, errors);

    expect(form.get('email')?.errors).toEqual({
      backend: ['translated_EMAIL_ALREADY_TAKEN translated_Email already taken']
    });

    expect(form.get('username')?.errors).toEqual({
      backend: ['translated_USERNAME_INVALID translated_Username is invalid']
    });

    // simulate user changing value
    form.get('email')?.setValue('new@example.com');

    expect(form.get('email')?.errors).toBeNull();
  });

  it('should add backend errors and clean them on value change but keep other errors', () => {
    const form = new FormGroup({
      username: new FormControl('oldUser')
    });

    const errors = {
      'username': [{code:'invalid',details: {'details': 'Username is invalid'}}]
    };

    form.get('username')!.setErrors({test: [{message: 'testerror'}]} as ValidationErrors);

    service.setBackendErrors(form, errors);

    expect(form.get('username')?.errors).toEqual({
      backend: ['translated_invalid translated_Username is invalid'],
      test: [{message: 'testerror'}]
    });

    // simulate user changing value
    form.get('username')?.setValue('newuser');

    expect(form.get('username')?.errors).toBeNull();
  });

  it('should unsubscribe all on cleanup', () => {
    const form = new FormGroup({
      email: new FormControl('test')
    });

    const errors = {
      'email': [{code: 'code', details: {'details' : 'some error'}}]
    };

    service.setBackendErrors(form, errors);

    const sub = (service as any).cleanupSubscriptions.get(form.get('email')!);
    const spy = jest.spyOn(sub, 'unsubscribe');

    service.cleanup();

    expect(spy).toHaveBeenCalled();
    expect((service as any).cleanupSubscriptions.size).toBe(0);
  });
});