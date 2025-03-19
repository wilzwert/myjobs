import { Component } from '@angular/core';
import { SessionService } from '../../core/services/session.service';
import { BehaviorSubject } from 'rxjs';
import { SessionInformation } from '../../core/model/session-information.interface';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-me',
  imports: [AsyncPipe],
  templateUrl: './me.component.html',
  styleUrl: './me.component.scss'
})
export class MeComponent {

  protected sessionInformation: BehaviorSubject<SessionInformation|null>;

  constructor(private sessionService: SessionService) {
    this.sessionInformation = sessionService.$getSessionInformation();
  }

  public logout(): void {
    this.sessionService.logOut();
  }
}
