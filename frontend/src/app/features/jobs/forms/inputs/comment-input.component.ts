import { Component } from "@angular/core";
import { AbstractControl, ReactiveFormsModule } from "@angular/forms";
import { MatButton } from "@angular/material/button";
import { MatFormField, MatHint, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { StatusIconComponent } from "@app/layout/shared/status-icon/status-icon.component";
import { EditorComponent, TINYMCE_SCRIPT_SRC } from "@tinymce/tinymce-angular";
import { BaseInputComponent } from "./baseinput.component";

@Component({
  selector: 'app-job-comment-input',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatHint, MatButton, EditorComponent, StatusIconComponent],
  providers: [
    { provide: TINYMCE_SCRIPT_SRC, useValue: 'tinymce/tinymce.min.js' }
  ],
  template: `@if(control) {
    <mat-form-field>
        <mat-label i18n="job comment|job comment input label@@input.job.comment.label">Comment</mat-label>
        <editor [init]="init" (onKeyDown)="updateRichText($event)" (onChange)="updateRichText($event)" [initialValue]="control.value"
        i18n-placeholder="job comment placeholder|job comment input placeholder@@input.job.comment.placeholder"
            placeholder="Job comment"
        ></editor>
        <mat-hint class="content-hint" align="end">
            <app-status-icon [isValid]="!control.invalid"/>
        </mat-hint>
        <textarea matInput placeholder="Job comment" [formControl]="control" class=""></textarea>
    </mat-form-field>}`
})
export class CommentInputComponent extends BaseInputComponent {
    override configure(): void {
        this.form.addControl('comment', this.fb.control(this.initialValue, []));
    }

    constructor() {
        super('comment');
    }
}