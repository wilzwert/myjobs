import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatMenuModule } from '@angular/material/menu';
import { RatingComponent } from '@features/jobs/rating/rating.component';
import { MatIcon } from '@angular/material/icon';
import { JobStatusComponent } from '@features/jobs/job-status/job-status.component';
import { MatCardModule } from '@angular/material/card';
import { RouterLink } from '@angular/router';
import { Job } from '@core/model/job.interface';
import { JobService } from '@core/services/job.service';
import { NotificationService } from '@core/services/notification.service';
import { UpdateJobStatusRequest } from '@core/model/update-job-status-request.interface';
import { ModalService } from '@core/services/modal.service';
import { UpdateJobRatingRequest } from '@core/model/update-job-rating-request.interface';
import { ConfirmDialogService } from '@core/services/confirm-dialog.service';
import { take, tap } from 'rxjs';
import { DatePipe } from '@angular/common';
import { MatIconButton } from '@angular/material/button';

@Component({
  selector: 'app-job-summary',
  imports: [MatMenuModule, RatingComponent, MatIcon, MatIconButton, JobStatusComponent, MatCardModule, RouterLink, DatePipe],
  templateUrl: './job-summary.component.html',
  styleUrl: './job-summary.component.scss'
})
export class JobSummaryComponent {
  @Input({ required: true }) job!: Job;
  @Output() deleted = new EventEmitter<Job>();

  constructor(private jobService:JobService, private notificationService: NotificationService, private modalService: ModalService, private confirmDialogService: ConfirmDialogService) {}

  editJobStatus(job: Job, status: string): void {
      // don't reload list as the edited job is replaced after update by the service
      this.jobService.updateJobStatus(job.id, { status: status } as UpdateJobStatusRequest).subscribe(
        (j) => {
          this.notificationService.confirmation($localize`:@@info.job.status.updated:Status updated successfully.`);
        }
      );
    }
  
    updateJobRating(job: Job, event: number): void {
      // don't reload list as the edited job is replaced after update by the service
      this.jobService.updateJobRating(job.id, { rating: event } as UpdateJobRatingRequest).subscribe(
        (j) => {
          this.notificationService.confirmation($localize`:@@info.job.rating.updated:Rating updated successfully.`);
        }
      );
    }
  
  editJob(event: Event, job: Job): void {
    // don't reload list as the edited job is replaced after update by the service
    this.modalService.openJobModal('job', job, () => { });
  }

  confirmDeleteJob(job: Job): void {
    this.jobService.deleteJob(job.id).pipe(
      take(1),
      tap(() => {
        this.deleted.emit(this.job);
      })
    ).subscribe();
    
  }
  
  deleteJob(job: Job): void {
    this.confirmDialogService.openConfirmDialog($localize`:@@warning.job.delete:Delete job "${job.title}" ? All data will be lost.`, () => this.confirmDeleteJob(job));
  }

  manageAttachments(event: Event, job: Job): void {
    // prevent routing to job detail 
    event.stopPropagation();
    // don't reload list; as the edited job is replaced after update directly by the service
    this.modalService.openJobModal('attachments', job, () => { });
  }
}
