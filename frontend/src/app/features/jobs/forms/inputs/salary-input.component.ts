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
    override configure(): void {
        console.log('configure salary with initial', this.initialValue);
        this.form.addControl('salary', this.fb.control(this.initialValue, []));
    }

    constructor() {
        super('salary');
    }
}