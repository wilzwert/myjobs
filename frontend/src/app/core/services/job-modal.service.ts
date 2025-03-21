import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { JobModalComponent } from '../../features/jobs/job-modal/job-modal.component';
import { Job } from '../model/job.interface';

@Injectable({
  providedIn: 'root'
})
export class JobModalService {

  constructor(private dialog: MatDialog) {}

  openJobModal(type: 'job' | 'attachments' | 'activity', job: Job | null = null, terminated: () => void) {
    const dialogRef: MatDialogRef<JobModalComponent> =  this.dialog.open(JobModalComponent, {
      width: '500px',
      data: { type: type, job: job, terminated: terminated }
    });

    dialogRef.afterClosed().subscribe(() => {
      if(terminated) {
        terminated();
      }
    })
  }

  // TODO
  openJobStepperModal() {
    /*
    return this.dialog.open(JobStepperModalComponent, {
      width: '600px'
    });*/
  }
}
