import { Component } from "@angular/core";
import { ReactiveFormsModule } from "@angular/forms";
import { MatFormField, MatHint, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { StatusIconComponent } from "@app/layout/shared/status-icon/status-icon.component";
import { BaseInputComponent } from "./baseinput.component";

@Component({
  selector: 'app-job-salary-input',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatHint, StatusIconComponent],
  templateUrl: './salary-input.component.html'
})
export class SalaryInputComponent extends BaseInputComponent {
    constructor() {
        super('salary');
    }
}