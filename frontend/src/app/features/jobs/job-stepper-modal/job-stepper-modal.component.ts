import { Component, EventEmitter, Inject, Output, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef } from '@angular/material/dialog';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { Job } from '../../../core/model/job.interface';
import { JobFormComponent } from '../job-form/job-form.component';
import { JobActivitiesFormComponent } from '../job-actitivies-form/job-actitivities-form.component';
import { JobAttachmentsFormComponent } from '../job-attachments-form/job-attachments-form.component';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-job-stepper-modal',
  imports: [ReactiveFormsModule, MatDialogContent, MatStepperModule, JobFormComponent, JobActivitiesFormComponent, JobAttachmentsFormComponent, MatDialogActions],
  templateUrl: './job-stepper-modal.component.html',
  styleUrl: './job-stepper-modal.component.scss'
})
export class JobStepperModalComponent {
  @ViewChild('matStepper') stepper!: MatStepper;

  @Output() terminated = new EventEmitter<boolean>();

  public loading = false;

  public job: Job | null = null;

  constructor(
    public dialogRef: MatDialogRef<JobStepperModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { type: string, job: Job|null }
  ) {}

  close(): void {
    this.dialogRef.close();
  }
  /*
  stepperInteracted(step: CdkStep): void {
    console.log(step);
  }*/

  hasJob() :boolean {
    return this.job !== null;
  }

  jobCreated(job: Job) :void {
    this.job = job;
    this.stepper.next();
  }

  next() :void {
    this.stepper.next();
  }

  terminate() :void {
    this.terminated.emit(true);
    this.close();
  }
}
