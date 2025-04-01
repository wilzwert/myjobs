import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { User } from '../../../core/model/user.interface';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { AuthValidators } from '../../../core/services/auth.validators';
import { MatInput } from '@angular/material/input';
import { UserService } from '../../../core/services/user.service';
import { EditUserRequest } from '../../../core/model/edit-user-request.interface';
import { catchError, take, throwError } from 'rxjs';
import { ApiError } from '../../../core/errors/api-error';
import { NotificationService } from '../../../core/services/notification.service';
import { ComponentInputData, ComponentInputDomainData } from '../../../core/model/component-input-data.interface';
import { BaseChildComponent } from '../../../core/component/base-child.component';

@Component({
  selector: 'app-user-form',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatHint, MatIcon],
  templateUrl: './user-form.component.html',
  styleUrl: './user-form.component.scss'
})
export class UserFormComponent extends BaseChildComponent implements OnInit {
  protected user!: User;

  protected form!: FormGroup;
  protected isSubmitting = false;

  constructor(
    private authValidators: AuthValidators,
    private fb: FormBuilder,
    private userService: UserService,
    private notificationService: NotificationService
  ) {
    super();
  }

  ngOnInit(): void {
    this.user  = this.data.user!;
    this.form = this.fb.group({
      email: [
        this.user.email, 
        [
          Validators.required,
          Validators.email
        ],
        this.authValidators.checkEmailExistsAsync(this.user.email).bind(this.authValidators)
      ],
      username: [
        this.user.username, 
        [
          Validators.required,
          Validators.minLength(5)
        ],
        this.authValidators.checkUsernameExistsAsync(this.user.username).bind(this.authValidators)
      ],
      firstName: [
        this.user.firstName, 
        [
          Validators.required,
          Validators.minLength(1)
        ]
      ],
      lastName: [
        this.user.lastName, 
        [
          Validators.required,
          Validators.minLength(1)
        ]
      ],
    });
  }

  get email() {
    return this.form.get('email');
  }

  get firstName() {
    return this.form.get('firstName');
  }

  get lastName() {
    return this.form.get('lastName');
  }

  get username() {
    return this.form.get('username');
  }

  submit() :void {
      if(!this.isSubmitting && this.form.valid) {

        const emailChanged: boolean = this.email?.value  !== this.user.email;

        this.isSubmitting = true;
        this.userService.editUser(this.form.value as EditUserRequest)
            .pipe(
              take(1),
              catchError(
                (error: ApiError) => {
                  this.isSubmitting = false;
                  this.fail();
                  return throwError(() => new Error(
                    `Edit failed : ${error.message}`
                  ));
                }
            ))
            .subscribe(() => {
                this.isSubmitting = false;
                this.notificationService.confirmation("Your information has been updated."+(emailChanged ? " Your new email address needs verification ; please check your emails." : ''));
                this.success();
            });
      }
    }
}
