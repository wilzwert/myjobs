import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatFormField, MatHint, MatInput, MatLabel } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { catchError, take, throwError } from 'rxjs';
import { NotificationService } from '../../../../core/services/notification.service';
import { PasswordValidator } from '../../../../core/validators/password-validator';
import { ConfirmPasswordValidator } from '../../../../core/validators/confirm-password-validator';
import { NewPasswordRequest } from '../../../../core/model/new-password-request.interface';
import { UserService } from '../../../../core/services/user.service';
import { StatusIconComponent } from "../../../../layout/shared/status-icon/status-icon.component";
import { ErrorProcessorService } from '../../../../core/services/error-processor.service';

@Component({
  selector: 'app-new-password',
  standalone: true,
  imports: [ReactiveFormsModule, MatInput, MatFormField, MatButton, MatLabel, MatHint, StatusIconComponent],
  templateUrl: './new-password.component.html',
  styleUrl: './new-password.component.scss'
})
export class NewPasswordComponent implements OnInit {

  public token!: String
  public form!: FormGroup;
  public isSubmitting = false;

  constructor(
    private activatedRoute: ActivatedRoute, 
    private fb: FormBuilder, 
    private userService: UserService, 
    private notificationService: NotificationService, 
    private router: Router,
    private errorProcessorService: ErrorProcessorService) {

  }

  get password() {
    return this.form.get('password');
  }

  get passwordConfirmation() {
    return this.form.get('passwordConfirmation');
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe((params) => {
      this.token = params['token'];
      this.form = this.fb.group({
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
      
    });
  }

  submit() :void {
    if(!this.isSubmitting && this.form.valid) {
      this.isSubmitting = true;
      this.userService.newPassword({password: this.password!.value, token: this.token} as NewPasswordRequest)
        .pipe(
          take(1),
          catchError(
            () => {
              this.isSubmitting = false;
              return this.errorProcessorService.processError(new Error($localize `:@@error.password.creation:Password creation failed`));
            }
        ))
        .subscribe(() => {
            this.isSubmitting = false;
            this.notificationService.confirmation($localize `:@@info.password.updated:Your password has been updated.` + ' '+ $localize `:@@info.login.possible:You may now log in.`);
            this.router.navigate(["/login"])
        });
    }
  }
}