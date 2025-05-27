import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SessionService } from '@core/services/session.service';
import { SessionInformation } from '@core/model/session-information.interface';
import { BehaviorSubject } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-home',
  imports: [RouterLink, AsyncPipe, MatCardModule, MatButton, MatIcon],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {

  loggedIn: boolean = false;
  sessionInformation$: BehaviorSubject<SessionInformation|null>|null = null;
  email: String = 'info@myjobs.wilzwert.com';

  constructor(private sessionService: SessionService){
    
  }

  ngOnInit(): void {
    this.loggedIn = this.sessionService.isLogged();
    this.sessionInformation$ = this.sessionService.$getSessionInformation();
  }
}
