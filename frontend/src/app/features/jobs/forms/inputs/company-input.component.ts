import { Component } from "@angular/core";
import { ReactiveFormsModule, Validators } from "@angular/forms";
import { MatFormField, MatHint, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { StatusIconComponent } from "@app/layout/shared/status-icon/status-icon.component";
import { BaseInputComponent } from "./baseinput.component";

@Component({
  selector: 'app-job-company-input',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatHint, StatusIconComponent],
  templateUrl: './company-input.component.html'
})
export class CompanyInputComponent extends BaseInputComponent {
    override configure(): void {
        this.form.addControl('company', this.fb.control(this.initialValue, [Validators.required, Validators.minLength(2)]));
    }

    constructor() {
        super('company');
    }
}