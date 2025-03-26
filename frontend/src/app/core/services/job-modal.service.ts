import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { JobModalComponent } from '../../features/jobs/job-modal/job-modal.component';
import { Job } from '../model/job.interface';
import { JobStepperModalComponent } from '../../features/jobs/job-stepper-modal/job-stepper-modal.component';

@Injectable({
  providedIn: 'root'
})
export class JobModalService {

  constructor(private dialog: MatDialog) {}

  openJobModal(type: 'job' | 'attachments' | 'activity', job: Job | null = null, terminated: () => void) {
    const dialogRef: MatDialogRef<JobModalComponent> =  this.dialog.open(JobModalComponent, {
      width: '80vw',
      maxWidth: '1000px',
      data: { type: type, job: job, terminated: terminated }
    });

    dialogRef.afterClosed().subscribe(() => {
      if(terminated) {
        terminated();
      }
    })
  }

  // TODO
  openJobStepperModal(terminated: () => void) {
    const dialogRef: MatDialogRef<JobStepperModalComponent> =  this.dialog.open(JobStepperModalComponent, {
      width: '80vw',
      maxWidth: '1000px',
      data: { terminated: terminated }
    });

    dialogRef.afterClosed().subscribe(() => {
      if(terminated) {
        terminated();
      }
    })
    /*
    return this.dialog.open(JobStepperModalComponent, {
      width: '600px'
    });*/
  }
}
