import { Component } from "@angular/core";
import { ReactiveFormsModule, Validators } from "@angular/forms";
import { MatFormField, MatHint, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { StatusIconComponent } from "@app/layout/shared/status-icon/status-icon.component";
import { BaseInputComponent } from "./baseinput.component";

@Component({
  selector: 'app-job-title-input',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatHint, StatusIconComponent],
  template: `@if(control) {
    <mat-form-field>
        <mat-label i18n="job title|job title input label@@input.job.title.label">Title</mat-label>
        <input matInput [formControl]="control" i18n-placeholder="job title placeholder|job title input placeholder@@input.job.title.placeholder" placeholder="Job title" >
        <mat-hint class="title-hint" align="end">
            <app-status-icon [isValid]="!control.invalid"/>    
        </mat-hint>
    </mat-form-field>}`
})
export class TitleInputComponent extends BaseInputComponent {
    override configure(): void {
        this.form.addControl('title', this.fb.control(this.initialValue, [Validators.required, Validators.minLength(3)]));
    }

    constructor() {
        super('title');
    }
}