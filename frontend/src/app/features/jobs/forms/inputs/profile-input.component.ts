import { Component } from "@angular/core";
import { AbstractControl, ReactiveFormsModule } from "@angular/forms";
import { MatButton } from "@angular/material/button";
import { MatFormField, MatHint, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { StatusIconComponent } from "@app/layout/shared/status-icon/status-icon.component";
import { EditorComponent, TINYMCE_SCRIPT_SRC } from "@tinymce/tinymce-angular";
import { BaseInputComponent } from "./baseinput.component";

@Component({
  selector: 'app-job-profile-input',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatHint, EditorComponent, StatusIconComponent],
  providers: [
    { provide: TINYMCE_SCRIPT_SRC, useValue: 'tinymce/tinymce.min.js' }
  ],
  template: `@if(control) {
        <mat-form-field>
            <mat-label i18n="job profile|job profile input label@@input.job.profile.label">Profile</mat-label>
            <editor [init]="init" (onKeyDown)="updateRichText($event)" (onChange)="updateRichText($event)" [initialValue]="control.value"
            i18n-placeholder="job profile placeholder|job profile input placeholder@@input.job.description.placeholder"
                placeholder="Job profile"
            ></editor>
            <mat-hint class="content-hint" align="end">
                <app-status-icon [isValid]="!control.invalid"/>
            </mat-hint>
            <textarea matInput placeholder="Job profile" [formControl]="control" class=""></textarea>
        </mat-form-field>}`
})
export class ProfileInputComponent extends BaseInputComponent {
    override configure(): void {
        console.log('configure profile with initial', this.initialValue);
        this.form.addControl('profile', this.fb.control(this.initialValue, []));
    }

    constructor() {
        super('profile');
    }
}