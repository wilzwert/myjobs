import { AfterViewInit, Component, EventEmitter, Inject, Output, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef } from '@angular/material/dialog';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { Job } from '../../../core/model/job.interface';
import { JobFormComponent } from '../job-form/job-form.component';
import { JobActivitiesFormComponent } from '../job-actitivies-form/job-actitivities-form.component';
import { JobAttachmentsFormComponent } from '../job-attachments-form/job-attachments-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { BaseChildComponent } from '../../../core/component/base-child.component';

@Component({
  selector: 'app-job-stepper',
  imports: [ReactiveFormsModule, MatButton, MatStepperModule, JobFormComponent, JobActivitiesFormComponent, JobAttachmentsFormComponent],
  templateUrl: './job-stepper.component.html',
  styleUrl: './job-stepper.component.scss'
})
export class JobStepperComponent extends BaseChildComponent  implements AfterViewInit {
  @ViewChild('matStepper') stepper!: MatStepper;

  public loading = false;

  public job: Job | null = null;

  constructor() {
    super();
  }

  ngAfterViewInit(): void {
    // no steps are completed at this moment
    this.stepper.steps.forEach(step => {
      step.completed = false;
    });
  }
 
  jobCreated(job: Job) :void {
    this.job = job;
    let step = this.stepper.steps.get(0);
    if(step !== null) {
      step!.completed = true;
    }
    this.stepper.next();
  }

  next() :void {
    this.stepper.next();
  }

  terminate() :void {
    this.success();
  }
}
