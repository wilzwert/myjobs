import { Component } from "@angular/core";
import { ReactiveFormsModule } from "@angular/forms";
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
  templateUrl: './profile-input.component.html'
})
export class ProfileInputComponent extends BaseInputComponent {
    constructor() {
        super('profile');
    }
}