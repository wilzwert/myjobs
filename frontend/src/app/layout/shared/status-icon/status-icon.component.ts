import { Component, Input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-status-icon',
  imports: [MatIcon],
  templateUrl: './status-icon.component.html',
  styleUrl: './status-icon.component.scss'
})
export class StatusIconComponent {
  @Input() isValid=false;
}
