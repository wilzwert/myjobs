import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmPasswordValidator } from '../../../core/validators/confirm-password-validator';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatHint, MatInput, MatLabel } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { PasswordValidator } from '../../../core/validators/password-validator';
import { JsonPipe } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { NewPasswordRequest } from '../../../core/model/new-password-request.interface';
import { catchError, take, throwError } from 'rxjs';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-new-password',
  standalone: true,
  imports: [ReactiveFormsModule, MatIcon, MatInput, MatFormField, MatButton, MatLabel, MatHint, JsonPipe],
  templateUrl: './new-password.component.html',
  styleUrl: './new-password.component.scss'
})
export class NewPasswordComponent implements OnInit {

  public token!: String
  public form!: FormGroup;
  public isSubmitting = false;

  constructor(private activatedRoute: ActivatedRoute, private fb: FormBuilder, private authService: AuthService, private notificationService: NotificationService, private router: Router) {

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
      this.authService.newPassword({password: this.password!.value, token: this.token} as NewPasswordRequest)
                .pipe(
                  take(1),
                  catchError(
                    () => {
                      this.isSubmitting = false;
                      return throwError(() => new Error(
                        'Password creation failed'
                      ));
                    }
                ))
                .subscribe(() => {
                    this.isSubmitting = false;
                    this.notificationService.confirmation("Your password has been updated. You may now log in.");
                    this.router.navigate(["/login"])
                });



    }
  }
}