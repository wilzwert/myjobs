import { AfterViewInit, Component, EventEmitter, Inject, OnInit, Output, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef } from '@angular/material/dialog';
import { MatStepper, MatStepperIntl, MatStepperModule } from '@angular/material/stepper';
import { Job } from '@core/model/job.interface';
import { JobFormComponent } from '@features/jobs/job-form/job-form.component';
import { JobActivitiesFormComponent } from '@features/jobs/job-actitivies-form/job-actitivities-form.component';
import { JobAttachmentsFormComponent } from '@features/jobs/job-attachments-form/job-attachments-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { BaseChildComponent } from '@core/component/base-child.component';
import { JobMetadata } from '@core/model/job-metadata.interface';
import { JobStepperIntl } from '@core/services/job-stepper-intl';

@Component({
  selector: 'app-job-stepper',
  imports: [ReactiveFormsModule, MatButton, MatStepperModule, JobFormComponent, JobActivitiesFormComponent, JobAttachmentsFormComponent],
  providers: [{provide: MatStepperIntl, useClass: JobStepperIntl}],
  templateUrl: './job-stepper.component.html',
  styleUrl: './job-stepper.component.scss'
})
export class JobStepperComponent extends BaseChildComponent  implements OnInit, AfterViewInit {
  @ViewChild('matStepper') stepper!: MatStepper;

  public loading = false;

  public job: Job | null = null;

  public jobMetadata:JobMetadata | null = null;

  constructor() {
    super();
  }
  ngOnInit(): void {
    if(this.data.metadata?.jobMetadata != null) {
      this.jobMetadata = this.data.metadata.jobMetadata;
    }
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
