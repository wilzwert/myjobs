import { Component } from "@angular/core";
import { ReactiveFormsModule, Validators } from "@angular/forms";
import { MatFormField, MatHint, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { StatusIconComponent } from "@app/layout/shared/status-icon/status-icon.component";
import { BaseInputComponent } from "./baseinput.component";

@Component({
  selector: 'app-job-title-input',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatHint, StatusIconComponent],
  templateUrl: './title-input.component.html'
})
export class TitleInputComponent extends BaseInputComponent {
    override configure(): void {
        this.form.addControl('title', this.fb.control(this.initialValue, [Validators.required, Validators.minLength(3)]));
    }

    constructor() {
        super('title');
    }
}