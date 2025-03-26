import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '../../core/services/auth.service';
import { RegistrationRequest } from '../../core/model/registration-request.interface';
import { catchError, take, throwError } from 'rxjs';
import { Router, RouterLink } from '@angular/router';
import { NotificationService } from '../../core/services/notification.service';
import { ApiError } from '../../core/errors/api-error';
import { AuthValidators } from '../../core/services/auth.validators';
import { NgxCaptchaModule } from 'ngx-captcha';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    ReactiveFormsModule,
    RouterLink,
    NgxCaptchaModule
  ],
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.scss'
})
export class RegistrationComponent {
  public form: FormGroup;
  public isSubmitting = false;

  constructor(
    private authService: AuthService,
    private authValidators: AuthValidators,
    private router: Router,
    private fb: FormBuilder,
    private notificationService: NotificationService
  ) {
    this.form = this.fb.group({
      email: [
        '', 
        [
          Validators.required,
          Validators.email
        ],
        this.authValidators.checkEmailExistsAsync.bind(this.authValidators)
      ],
      username: [
        '', 
        [
          Validators.required,
          Validators.minLength(5)
        ],
        this.authValidators.checkUsernameExistsAsync.bind(this.authValidators)
      ],
      firstName: [
        '', 
        [
          Validators.required,
          Validators.minLength(1)
        ]
      ],
      lastName: [
        '', 
        [
          Validators.required,
          Validators.minLength(1)
        ]
      ],
      password: [
        '',
        [
          Validators.required,
          Validators.pattern('((?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,30})')
        ]
      ]
    });
  }

  get email() {
    return this.form.get('email');
  }

  get username() {
    return this.form.get('username');
  }

  get password() {
    return this.form.get('password');
  }

  get firstName() {
    return this.form.get('firstName');
  }

  get lastName() {
    return this.form.get('lastName');
  }

  submit() :void {
    if(!this.isSubmitting && this.form.valid) {
      this.isSubmitting = true;
      this.authService.register(this.form.value as RegistrationRequest)
      .pipe(
        take(1),
        catchError(
          (error: ApiError) => {
            this.isSubmitting = false;
            return throwError(() => new Error(
              'Registration failed. '+(error.httpStatus === 409 ? "Email ou nom d'utilisateur déjà utilisé" : 'Une erreur est survenue')
            ));
          }
      ))
      .subscribe(() => {
          this.isSubmitting = false;
          this.notificationService.confirmation("Registration completed successfully, you may now log in.");
          this.router.navigate(["/login"])
      });
    }
  }
}