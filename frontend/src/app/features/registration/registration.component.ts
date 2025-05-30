import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '@core/services/auth.service';
import { RegistrationRequest } from '@core/model/registration-request.interface';
import { catchError, take, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { NotificationService } from '@core/services/notification.service';
import { ApiError } from '@core/errors/api-error';
import { AuthValidators } from '@core/services/auth.validators';
import { NgxCaptchaModule } from 'ngx-captcha';
import { PasswordValidator } from '@core/validators/password-validator';
import { UserFormComponent } from "@features/user/user-form/user-form.component";
import { ErrorProcessorService } from '@core/services/error-processor.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    ReactiveFormsModule,
    NgxCaptchaModule,
    UserFormComponent
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
    private notificationService: NotificationService,
    private errorProcessorService: ErrorProcessorService
  ) {
    this.form = this.fb.group({
      email: [
        '', 
        [
          Validators.required,
          Validators.email
        ],
        this.authValidators.checkEmailExistsAsync().bind(this.authValidators)
      ],
      username: [
        '', 
        [
          Validators.required,
          Validators.minLength(5)
        ],
        this.authValidators.checkUsernameExistsAsync().bind(this.authValidators)
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
          PasswordValidator
        ]
      ]
    });
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
            return this.errorProcessorService.processError(error);
          }
      ))
      .subscribe(() => {
          this.isSubmitting = false;
          this.notificationService.confirmation($localize `:@@message.registration.success:Registration completed successfully, you may now log in.`);
          this.router.navigate(["/login"])
      });
    }
  }
}