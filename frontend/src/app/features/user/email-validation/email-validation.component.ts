import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../core/services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ValidateEmailRequest } from '../../../core/model/validate-email-request.interface';
import { catchError, take, throwError } from 'rxjs';
import { NotificationService } from '../../../core/services/notification.service';
import { SessionService } from '../../../core/services/session.service';

@Component({
  selector: 'app-email-validation',
  imports: [],
  templateUrl: './email-validation.component.html',
  styleUrl: './email-validation.component.scss'
})
export class EmailValidationComponent implements OnInit {

  constructor(private userService: UserService, private activatedRoute: ActivatedRoute, private router: Router, private notificationService: NotificationService, private sessionService: SessionService) {}

  private redirect(): void {
    if(this.sessionService.isLogged()) {
      this.router.navigate(["/jobs"])
    }
    else {
      this.router.navigate(["/login"])
    }
  }
 
  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe((params) => {
      if(params['code'] == undefined || params['code'] == null || params['code'] == '') {
        this.router.navigate([""]);
      }
      else {
        this.userService.validateEmail({code: params['code']} as ValidateEmailRequest).pipe(
          take(1),
          catchError(
            () => {
              this.redirect();
              return throwError(() => new Error(
                'Email validation failed'
              ));
            }
        )).subscribe(() => {
          this.notificationService.confirmation($localize `:@@info.email.validated:Your email has been validated.`);
          this.redirect();
        })
      }
    });
  }
}