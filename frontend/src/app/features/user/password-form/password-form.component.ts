import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '@core/services/user.service';
import { NotificationService } from '@core/services/notification.service';
import { PasswordValidator } from '@core/validators/password-validator';
import { ConfirmPasswordValidator } from '@core/validators/confirm-password-validator';
import { ChangePasswordRequest } from '@core/model/change-password-request.interface';
import { catchError, take, throwError } from 'rxjs';
import { MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatButton } from '@angular/material/button';
import { MatInput } from '@angular/material/input';
import { ApiError } from '@core/errors/api-error';
import { BaseChildComponent } from '@core/component/base-child.component';
import { StatusIconComponent } from "@layout/shared/status-icon/status-icon.component";
import { ErrorProcessorService } from '@core/services/error-processor.service';

@Component({
  selector: 'app-password-form',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatButton, MatLabel, MatHint, StatusIconComponent],
  templateUrl: './password-form.component.html',
  styleUrl: './password-form.component.scss'
})
export class PasswordFormComponent extends BaseChildComponent implements OnInit {

  public form!: FormGroup;
  public isSubmitting = false;

  constructor(private fb: FormBuilder, private userService: UserService, private notificationService: NotificationService, private errorProcessService: ErrorProcessorService) {
    super();
  }

  get oldPassword() {
    return this.form.get('oldPassword');
  }

  get password() {
    return this.form.get('password');
  }

  get passwordConfirmation() {
    return this.form.get('passwordConfirmation');
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      oldPassword: [
        '', 
        [
          Validators.required,
        ],
      ],
      password: [
        '', 
        [
          Validators.required,
          PasswordValidator
        ],
      ],
      passwordConfirmation: [
        '',
        [
          Validators.required,
          // PasswordValidator
        ]
      ]
    },
    { validators: ConfirmPasswordValidator}
    );
  }

  submit() :void {
    if(!this.isSubmitting && this.form.valid) {
      this.isSubmitting = true;
      this.userService.changePassword({password: this.password!.value, oldPassword: this.oldPassword!.value } as ChangePasswordRequest)
                .pipe(
                  take(1),
                  catchError(
                    (error: ApiError) => {
                      this.isSubmitting = false;
                      this.fail();
                      return this.errorProcessService.processError(new Error(`Password update failed : ${error.message}`));
                    }
                ))
                .subscribe(() => {
                    this.isSubmitting = false;
                    this.notificationService.confirmation($localize `:@@info.password.updated:Your password has been updated.`);
                    this.success();
                });
    }
  }
}