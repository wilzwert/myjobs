import { AfterViewInit, Component, EventEmitter, Inject, Output, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef } from '@angular/material/dialog';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { Job } from '../../../core/model/job.interface';
import { JobFormComponent } from '../job-form/job-form.component';
import { JobActivitiesFormComponent } from '../job-actitivies-form/job-actitivities-form.component';
import { JobAttachmentsFormComponent } from '../job-attachments-form/job-attachments-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-job-stepper-modal',
  imports: [ReactiveFormsModule, MatDialogContent, MatButton, MatStepperModule, JobFormComponent, JobActivitiesFormComponent, JobAttachmentsFormComponent, MatDialogActions],
  templateUrl: './job-stepper-modal.component.html',
  styleUrl: './job-stepper-modal.component.scss'
})
export class JobStepperModalComponent implements AfterViewInit {
  @ViewChild('matStepper') stepper!: MatStepper;

  @Output() terminated = new EventEmitter<boolean>();

  public loading = false;

  public job: Job | null = null;

  constructor(
    public dialogRef: MatDialogRef<JobStepperModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { type: string, job: Job|null }
  ) {}

  ngAfterViewInit(): void {
    // no steps are completed at this moment
    this.stepper.steps.forEach(step => {
      step.completed = false;
    });
  }

  close(): void {
    console.log('closeDIalogref');
    this.dialogRef.close();
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
    this.terminated.emit(true);
    this.close();
  }
}
