import { Component } from "@angular/core";
import { ReactiveFormsModule, Validators } from "@angular/forms";
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
  templateUrl: './description-input.component.html'
})
export class DescriptionInputComponent extends BaseInputComponent {
    override configure(): void {
        this.form.addControl('description', this.fb.control(this.initialValue, [Validators.required]));
    }

    constructor() {
        super('description');
    }
}