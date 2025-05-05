import { Component, Input } from '@angular/core';
import { AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-input-backend-errors',
  imports: [],
  templateUrl: './input-backend-errors.component.html',
  styleUrl: './input-backend-errors.component.scss'
})
export class InputBackendErrorsComponent {
  @Input() control?: AbstractControl|null = null;
}
