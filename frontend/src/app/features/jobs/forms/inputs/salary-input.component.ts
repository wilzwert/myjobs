import { Component } from "@angular/core";
import { ReactiveFormsModule } from "@angular/forms";
import { MatFormField, MatHint, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { StatusIconComponent } from "@app/layout/shared/status-icon/status-icon.component";
import { BaseInputComponent } from "./baseinput.component";

@Component({
  selector: 'app-job-salary-input',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatHint, StatusIconComponent],
  template: `@if(control) {
    <mat-form-field>
        <mat-label i18n="job salary|job salary input label@@input.job.salary.label">Salary</mat-label>
        <input matInput [formControl]="control" i18n-placeholder="job salary placeholder|job salary input placeholder@@input.job.salary.placeholder" placeholder="Salary">
        <mat-hint class="salary-hint" align="end">
            <app-status-icon [isValid]="!control.invalid"/>
        </mat-hint>
    </mat-form-field>}`
})
export class SalaryInputComponent extends BaseInputComponent {
    override configure(): void {
        console.log('configure salary with initial', this.initialValue);
        this.form.addControl('salary', this.fb.control(this.initialValue, []));
    }

    constructor() {
        super('salary');
    }
}