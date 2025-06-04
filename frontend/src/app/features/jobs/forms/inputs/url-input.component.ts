import { Component } from "@angular/core";
import { ReactiveFormsModule, Validators } from "@angular/forms";
import { MatFormField, MatHint, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { StatusIconComponent } from "@app/layout/shared/status-icon/status-icon.component";
import { BaseInputComponent } from "./baseinput.component";

@Component({
  selector: 'app-job-url-input',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatHint, StatusIconComponent],
  template: `@if(control) {
    <mat-form-field>
        <mat-label i18n="url|url input label@@input.url.label">Url</mat-label>
        <input matInput [formControl]="control" type="url" i18n-placeholder="url placeholder|url input placeholder@@input.url.placeholder" placeholder="Job url">
        <mat-hint class="url-hint" align="end">
            <app-status-icon [isValid]="!control.invalid"/>
        </mat-hint>
    </mat-form-field>}`
})
export class UrlInputComponent extends BaseInputComponent {
    override configure(): void {
        this.form.addControl('url', this.fb.control(this.initialValue, [Validators.required, Validators.pattern('(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)')]));
    }

    constructor() {
        super('url');
    }
}