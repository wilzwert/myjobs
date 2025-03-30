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

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe((params) => {
      console.log(params['code']);
      this.userService.validateEmail({code: params['code']} as ValidateEmailRequest).pipe(
        take(1),
        catchError(
          (error) => {
            console.log(error);
            return throwError(() => new Error(
              'Email validation failed'
            ));
          }
      )).subscribe(() => {
        this.notificationService.confirmation("Your email has been validated.");
        if(this.sessionService.isLogged()) {
          this.router.navigate(["/jobs"])
        }
        else {
          this.router.navigate(["/login"])
        }
      })
    });
  }

}
