import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogContent, MatDialogActions } from '@angular/material/dialog';
import { JobFormComponent } from '../job-form/job-form.component';
import { JobActitivyFormComponent } from '../job-actitivy-form/job-actitivy-form.component';
import { JobAttachmentsFormComponent } from '../job-attachments-form/job-attachments-form.component';
import { Job } from '../../../core/model/job.interface';

@Component({
  selector: 'app-job-modal',
  imports: [MatDialogContent, MatDialogActions, JobFormComponent, JobActitivyFormComponent, JobAttachmentsFormComponent],
  templateUrl: './job-modal.component.html',
  styleUrl: './job-modal.component.scss'
})
export class JobModalComponent {
  @Output() terminated = new EventEmitter<boolean>();

  constructor(
    public dialogRef: MatDialogRef<JobModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { type: string, job: Job|null }
  ) {console.log('JobModalComponent', data.job)}

  close(): void {
    this.dialogRef.close();
  }

  terminate() :void {
    this.terminated.emit(true);
    this.close();
  }
}
