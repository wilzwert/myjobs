import { Component, OnDestroy, OnInit } from '@angular/core';
import { User } from '../../../core/model/user.interface';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthValidators } from '../../../core/services/auth.validators';
import { UserService } from '../../../core/services/user.service';
import { EditUserRequest } from '../../../core/model/edit-user-request.interface';
import { catchError, throwError } from 'rxjs';
import { ApiError } from '../../../core/errors/api-error';
import { NotificationService } from '../../../core/services/notification.service';
import { BaseChildComponent } from '../../../core/component/base-child.component';
import { FormErrorService } from '../../../core/services/form-error.service';
import { UserFormComponent } from "../user-form/user-form.component";
import { LocaleService } from '../../../core/services/locale.service';

@Component({
  selector: 'app-user-edit',
  imports: [ReactiveFormsModule, UserFormComponent],
  templateUrl: './user-edit.component.html',
  styleUrl: './user-edit.component.scss'
})
export class UserEditComponent extends BaseChildComponent implements OnInit, OnDestroy {
  protected user!: User;

  protected form!: FormGroup;
  protected isSubmitting = false;
  
  constructor(
    private authValidators: AuthValidators,
    private fb: FormBuilder,
    private userService: UserService,
    private notificationService: NotificationService,
    private formErrorService: FormErrorService
  ) {
    super();
  }
  ngOnDestroy(): void {
    this.formErrorService.cleanup(); // free subscriptions 
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
      ]
    });
  }

  get email() {
    return this.form.get('email');
  }

  submit() :void {
      if(!this.isSubmitting && this.form.valid) {

        const emailChanged: boolean = this.email?.value  !== this.user.email;

        this.isSubmitting = true;
        this.userService.editUser(this.form.value as EditUserRequest)
            .pipe(
              // take(1),
              catchError(
                (error: ApiError) => {
                  this.isSubmitting = false;
                  
                  this.formErrorService.setBackendErrors(this.form, error.errors);


                  return throwError(() => error);
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
