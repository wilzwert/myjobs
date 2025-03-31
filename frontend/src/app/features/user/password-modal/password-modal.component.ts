import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { NotificationService } from '../../../core/services/notification.service';
import { PasswordValidator } from '../../../core/validators/password-validator';
import { ConfirmPasswordValidator } from '../../../core/validators/confirm-password-validator';
import { ChangePasswordRequest } from '../../../core/model/change-password-request.interface';
import { catchError, take, throwError } from 'rxjs';
import { MatDialogRef } from '@angular/material/dialog';
import { MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { ApiError } from '../../../core/errors/api-error';

@Component({
  selector: 'app-password-modal',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatButton, MatLabel, MatIcon, MatHint],
  templateUrl: './password-modal.component.html',
  styleUrl: './password-modal.component.scss'
})
export class PasswordModalComponent implements OnInit {

  public form!: FormGroup;
  public isSubmitting = false;

  constructor(public dialogRef: MatDialogRef<PasswordModalComponent>, private fb: FormBuilder, private userService: UserService, private notificationService: NotificationService) {

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
                      return throwError(() => new Error(
                        `Password update failed : ${error.message}`
                      ));
                    }
                ))
                .subscribe(() => {
                    this.isSubmitting = false;
                    this.notificationService.confirmation("Your password has been updated.");
                    this.dialogRef.close();
                });
    }
  }
}