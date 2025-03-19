import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { SessionService } from '../../core/services/session.service';
import { AuthService } from '../../core/services/auth.service';
import { catchError, throwError } from 'rxjs';
import { ApiError } from '../../core/errors/api-error';
import { LoginRequest } from '../../core/model/login-request.interface';
import { SessionInformation } from '../../core/model/session-information.interface';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  public form:FormGroup;
  public isSubmitting = false;

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private sessionService: SessionService) {
      this.form = this.fb.group({
        email: [
          '', 
          [
            Validators.required,
            Validators.email
          ]
        ],
        password: [
          '',
          [
            Validators.required,
            Validators.min(3)
          ]
        ]
      });
  }

  public submit() :void {
    if(!this.isSubmitting) {
      this.isSubmitting = true;
      const loginRequest: LoginRequest = this.form.value as LoginRequest;
      this.authService.login(loginRequest)
        .pipe(
          catchError(
            (error: ApiError) => {
              this.isSubmitting = false;
              return throwError(() => new Error(
                'La connexion a échoué.'+(error.httpStatus === 401 ? ' Email ou mot de passe incorrect' : '')
              ));
            }
          )
        )
        .subscribe(
          (response: SessionInformation) => {
            this.isSubmitting = false;
            this.sessionService.logIn(response);
            this.router.navigate(['/jobs'])
          }
        )
    }
  }
}
