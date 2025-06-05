import { Component } from "@angular/core";
import { AbstractControl, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatButton } from "@angular/material/button";
import { MatFormField, MatHint, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { StatusIconComponent } from "@app/layout/shared/status-icon/status-icon.component";
import { EditorComponent, TINYMCE_SCRIPT_SRC } from "@tinymce/tinymce-angular";
import { BaseInputComponent } from "./baseinput.component";

@Component({
  selector: 'app-job-description-input',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatHint, EditorComponent, StatusIconComponent],
  providers: [
    { provide: TINYMCE_SCRIPT_SRC, useValue: 'tinymce/tinymce.min.js' }
  ],
  template: `@if(control) {
        <mat-form-field>
                <mat-label i18n="job description|job description input label@@input.job.description.label">Description</mat-label>
                <editor [init]="init" (onKeyDown)="updateRichText($event)"  (onChange)="updateRichText($event)" [initialValue]="control.value"
                    i18n-placeholder="job description placeholder|job description input placeholder@@input.job.description.placeholder"
                    placeholder="Job description"
                ></editor>
                <mat-hint class="content-hint" align="end">
                    <app-status-icon [isValid]="!control.invalid"/>
                </mat-hint>
                <textarea matInput placeholder="Job description" [formControl]="control" class=""></textarea>
            </mat-form-field>}`
})
export class DescriptionInputComponent extends BaseInputComponent {
    override configure(): void {
        this.form.addControl('description', this.fb.control(this.initialValue, [Validators.required]));
    }

    constructor() {
        super('description');
    }
}