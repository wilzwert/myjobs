import { Component, Input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { Job, JobStatus } from '../../../core/model/job.interface';
import { JobService } from '../../../core/services/job.service';
import { NotificationService } from '../../../core/services/notification.service';
import { UpdateJobStatusRequest } from '../../../core/model/update-job-status-request.interface';
import { StatusLabelPipe } from '../../../core/pipe/status-label.pipe';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-job-status',
  imports: [MatMenuModule, MatIcon, StatusLabelPipe, DatePipe],
  templateUrl: './job-status.component.html',
  styleUrl: './job-status.component.scss'
})
export class JobStatusComponent {
  @Input({ required: true }) job!: Job;

  statusKeys: string[] = [];

  constructor(private jobService: JobService, private notificationService: NotificationService) {
    this.statusKeys = Object.keys(JobStatus);
  }

  editJobStatus(job: Job, status: string): void {
    // don't reload list as the edited job is replaced after update by the service
    this.jobService.updateJobStatus(job.id, { status: status } as UpdateJobStatusRequest).subscribe(
      (j) => {
        this.notificationService.confirmation($localize`:@@info.job.status.updated:Status updated successfully.`);
      }
    );
  }

  get icon(): string {
    switch (this.job.status) {
      case JobStatus.CREATED:
        return 'send';
      case JobStatus.PENDING:
        return 'hourglass_top';
      case JobStatus.RELAUNCHED:
        return 'refresh';
      case JobStatus.APPLICANT_REFUSED:
        return 'check_circle';
      case JobStatus.COMPANY_REFUSED:
        return 'cancel';
      case JobStatus.ACCEPTED:
        return 'check';
      
      default:
        return 'help';
    }
  }

}
