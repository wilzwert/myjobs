import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { NotificationService } from '../../../core/services/notification.service';
import { catchError, take, throwError } from 'rxjs';
import { ApiError } from '../../../core/errors/api-error';
import { RegistrationRequest } from '../../../core/model/registration-request.interface';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NgxCaptchaModule } from 'ngx-captcha';
import { ResetPasswordRequest } from '../../../core/model/reset-password-request.interface';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [MatButtonModule, MatFormFieldModule, MatIconModule, MatInputModule, ReactiveFormsModule, RouterLink, NgxCaptchaModule],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss'
})
export class ResetPasswordComponent {
  public form: FormGroup;
  public isSubmitting = false;

  constructor(
    private authService: AuthService,
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
      ],
    });
  }

  get email() {
    return this.form.get('email');
  }

  submit() :void {
    if(!this.isSubmitting && this.form.valid) {
          this.isSubmitting = true;
          this.authService.resetPassword(this.form.value as ResetPasswordRequest)
          .pipe(
            take(1),
            catchError(
              () => {
                this.isSubmitting = false;
                return throwError(() => new Error(
                  'Password request failed'
                ));
              }
          ))
          .subscribe(() => {
              this.isSubmitting = false;
              this.notificationService.confirmation("Your request has been processed. If your email is linked to an account, an email has been sent. Please check your emails for further instructions.");
              this.router.navigate(["/"])
          });
        }
  }
}
