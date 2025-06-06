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
import { ModalService } from '@core/services/modal.service';
import { ConfirmDialogService } from '@core/services/confirm-dialog.service';
import { take, tap } from 'rxjs';
import { DatePipe } from '@angular/common';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { JobCommentComponent } from "../job-comment/job-comment.component";
import { JobEditableFieldComponent } from "../job-editable-field/job-editable-field.component";

@Component({
  selector: 'app-job-summary',
  imports: [MatMenuModule, RatingComponent, MatIcon, MatTooltip, MatIconButton, JobStatusComponent, MatCardModule, RouterLink, DatePipe, JobCommentComponent, JobEditableFieldComponent],
  templateUrl: './job-summary.component.html',
  styleUrl: './job-summary.component.scss'
})
export class JobSummaryComponent {
  @Input({ required: true }) job!: Job;
  @Input() context: 'list' | 'detail' = 'list';
  @Output() deleted = new EventEmitter<Job>();
  @Output() jobChanged = new EventEmitter<Job>();

  constructor(private jobService:JobService, private notificationService: NotificationService, private modalService: ModalService, private confirmDialogService: ConfirmDialogService) {}

  onJobChanged(job: Job): void {
    this.job = job;
    this.jobChanged.emit(job);
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
