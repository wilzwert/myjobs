import { Component } from "@angular/core";
import { ReactiveFormsModule, Validators } from "@angular/forms";
import { MatFormField, MatHint, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { StatusIconComponent } from "@app/layout/shared/status-icon/status-icon.component";
import { BaseInputComponent } from "./baseinput.component";

@Component({
  selector: 'app-job-company-input',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatHint, StatusIconComponent],
  template: `@if(control) {
    <mat-form-field>
        <mat-label i18n="job company|job company input label@@input.job.company.label">Company</mat-label>
        <input matInput [formControl]="control" i18n-placeholder="job company placeholder|job company input placeholder@@input.job.company.placeholder" placeholder="Company">
        <mat-hint class="company-hint" align="end">
            <app-status-icon [isValid]="!control.invalid"/>
        </mat-hint>
    </mat-form-field>}`
})
export class CompanyInputComponent extends BaseInputComponent {
    override configure(): void {
        this.form.addControl('company', this.fb.control(this.initialValue, [Validators.required, Validators.minLength(2)]));
    }

    constructor() {
        super('company');
    }
}