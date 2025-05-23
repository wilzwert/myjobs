import { Component, OnInit } from '@angular/core';
import { Observable, take, tap } from 'rxjs';
import { AsyncPipe, DatePipe } from '@angular/common';
import { MatPaginatorIntl, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';
import { Page } from '../../../core/model/page.interface';
import { Job, JobStatus } from '../../../core/model/job.interface';
import { JobService } from '../../../core/services/job.service';
import { RouterLink } from '@angular/router';
import { ModalService } from '../../../core/services/modal.service';
import { StatusLabelPipe } from '../../../core/pipe/status-label.pipe';
import { MatFormField, MatHint, MatLabel, MatOption, MatSelect, MatSelectChange } from '@angular/material/select';
import { UpdateJobStatusRequest } from '../../../core/model/update-job-status-request.interface';
import { MatButton } from '@angular/material/button';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { NotificationService } from '../../../core/services/notification.service';
import { RatingComponent } from '../rating/rating.component';
import { UpdateJobRatingRequest } from '../../../core/model/update-job-rating-request.interface';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatInput } from '@angular/material/input';
import { JobMetadata } from '../../../core/model/job-metadata.interface';
import { StatusIconComponent } from "../../../layout/shared/status-icon/status-icon.component";
import { CustomPaginatorIntl } from '../../../core/services/custom-paginator-intl';
import { User } from '../../../core/model/user.interface';
import { UserService } from '../../../core/services/user.service';


@Component({
  selector: 'app-jobs',
  imports: [AsyncPipe, DatePipe, MatCardModule, MatPaginatorModule, RatingComponent, RouterLink, StatusLabelPipe, MatFormField, MatInput, MatLabel, MatSelect, MatOption, MatButton, FormsModule, ReactiveFormsModule, MatHint, StatusIconComponent],
  providers: [{ provide: MatPaginatorIntl, useClass: CustomPaginatorIntl }],
  templateUrl: './jobs.component.html',
  styleUrl: './jobs.component.scss'
})
export class JobsComponent implements OnInit {

  public urlForm: FormGroup | undefined;
  public urlFormLoading = false;

  public jobs$!: Observable<Page<Job>>;
  protected user$: Observable<User>;

  public currentPage: number;
  public currentPageSize: number;
  public currentStatus: keyof typeof JobStatus | null = null;
  public currentSort: string = 'createdAt,desc';
  public filterLate = false;

  statusKeys: string[] = [];

  constructor(private fb: FormBuilder, private userService: UserService, private jobService: JobService, private modalService: ModalService, private confirmDialogService: ConfirmDialogService, private notificationService: NotificationService) {
    this.currentPage = jobService.getCurrentPage();
    if (this.currentPage == -1) {
      this.currentPage = 0;
    }
    this.currentPageSize = jobService.getItemsPerPage();
    if (this.currentPageSize == -1) {
      this.currentPageSize = 10;
    }

    this.statusKeys = Object.keys(JobStatus);
    this.user$ = this.userService.getUser();
  }

  get url() {
    return this.urlForm?.get('url');
  }

  ngOnInit(): void {
    this.jobs$ = this.jobService.getAllJobs(this.currentPage, this.currentPageSize, this.currentStatus, this.filterLate, this.currentSort);
    this.urlForm = this.fb.group({
      url: [
        '',
        [
          Validators.required,
          Validators.pattern('(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)')
        ]
      ],
    });
  }

  setStatus(event: MatSelectChange): void {
    // having two properties to handle status or "filter-late" filters may seem unclean, as they are exclusive at the moment
    // but it actually allow us to change our mind in the future and use both filters cumulatively 
    this.currentStatus = event.value === "filter-late" ? null : event.value;
    this.filterLate = event.value === "filter-late";
    this.reloadJobs();
  }

  updateJobStatus(job: Job, event: MatSelectChange): void {
    // don't reload list as the edited job is replaced after update by the service
    this.jobService.updateJobStatus(job.id, { status: event.value } as UpdateJobStatusRequest).subscribe(
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

  changeSort(): void {
    this.reloadJobs();
  }

  handlePageEvent(event: PageEvent) {
    this.jobs$ = this.jobService.getAllJobs(event.pageIndex, event.pageSize, this.currentStatus, this.filterLate, this.currentSort);
    this.currentPage = event.pageIndex;
    this.currentPageSize = event.pageSize;
  }

  reloadJobs(job: Job | null = null): void {
    this.currentPage = 0;
    this.jobs$ = this.jobService.getAllJobs(this.currentPage, this.currentPageSize, this.currentStatus, this.filterLate, this.currentSort);
  }

  createJobWithMetadata(): void {
    this.jobService.getJobMetadata(this.url?.value).subscribe((metadata: JobMetadata) => {
      this.modalService.openJobStepperModal(() => this.reloadJobs(), { jobMetadata: metadata });
    });
  }

  createJob(): void {
    this.modalService.openJobStepperModal(() => this.reloadJobs());
  }

  editJob(event: Event, job: Job): void {
    // don't reload list as the edited job is replaced after update by the service
    this.modalService.openJobModal('job', job, () => { });
  }

  confirmDeleteJob(job: Job): void {
    this.jobService.deleteJob(job.id).pipe(
      take(1),
      tap(() => {
        this.notificationService.confirmation($localize`:@@job.deleted:Job successfully deleted.`);
        this.reloadJobs();
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