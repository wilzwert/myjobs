import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { StatusIconComponent } from '@layout/shared/status-icon/status-icon.component';
import { InputBackendErrorsComponent } from "@layout/shared/input-backend-errors/input-backend-errors.component";
import { MatButton } from '@angular/material/button';
import { LocaleService } from '@core/services/locale.service';
import { PasswordValidator } from '@core/validators/password-validator';
import { User } from '@core/model/user.interface';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-user-form',
  imports: [ReactiveFormsModule, MatButton, MatFormField, MatInput, MatLabel, MatHint, StatusIconComponent, InputBackendErrorsComponent, RouterLink],
  templateUrl: './user-form.component.html',
  styleUrl: './user-form.component.scss'
})
export class UserFormComponent implements OnInit {
  @Input() form!: FormGroup;
  @Input() user: User | null = null;
  @Input() isSubmitting = false;
  @Input() showPassword = false;
  @Input() showJobFollowUpReminderDays = false;
  @Input() submitLabel: 'Register' | 'Save' = 'Save';
  @Output() submitted = new EventEmitter<void>();

  public currentLang: string;
  
  constructor(private localeService: LocaleService) {
    this.currentLang = this.localeService.currentLocale.toUpperCase();
  }
  ngOnInit(): void {
    this.form.addControl('lang', new FormControl(this.currentLang, []));
    if(this.showPassword) {
      this.form.addControl('password', new FormControl('', [
        Validators.required,
        PasswordValidator
      ]));
    }
    if(this.showJobFollowUpReminderDays) {
      this.form.addControl('jobFollowUpReminderDays', new FormControl((this.user ? this.user.jobFollowUpReminderDays : 7), [
        Validators.required
      ]));
    }
  }

  get email() {
    return this.form.get('email');
  }

  get firstName() {
    return this.form.get('firstName');
  }

  get lastName() {
    return this.form.get('lastName');
  }

  get username() {
    return this.form.get('username');
  }

  get password() {
    return this.form.get('password');
  }

  get jobFollowUpReminderDays() {
    return this.form.get('jobFollowUpReminderDays');
  }

  get lang() {
    return this.form.get('lang');
  }

  submit() :void {
      this.submitted.emit();
  }
}
