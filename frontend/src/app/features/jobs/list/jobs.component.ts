import { Component, OnInit } from '@angular/core';
import { Observable, take, tap } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { MatPaginatorIntl, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';
import { Page } from '../../../core/model/page.interface';
import { Job, JobStatus } from '../../../core/model/job.interface';
import { JobService } from '../../../core/services/job.service';
import { ModalService } from '../../../core/services/modal.service';
import { StatusLabelPipe } from '../../../core/pipe/status-label.pipe';
import { MatFormField, MatHint, MatLabel } from '@angular/material/select';
import { MatButton } from '@angular/material/button';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { NotificationService } from '../../../core/services/notification.service';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatInput } from '@angular/material/input';
import {MatRippleModule} from '@angular/material/core';
import { MatMenuModule } from '@angular/material/menu';
import { JobMetadata } from '../../../core/model/job-metadata.interface';
import { StatusIconComponent } from "../../../layout/shared/status-icon/status-icon.component";
import { CustomPaginatorIntl } from '../../../core/services/custom-paginator-intl';
import { User } from '../../../core/model/user.interface';
import { UserService } from '../../../core/services/user.service';
import { MatIcon } from '@angular/material/icon';
import { JobSummaryComponent } from '../job-summary/job-summary.component';
import { ComponentInputData, ComponentInputDomainData } from '../../../core/model/component-input-data.interface';


@Component({
  selector: 'app-jobs',
  imports: [AsyncPipe, MatMenuModule, MatRippleModule, MatCardModule, MatPaginatorModule, MatIcon, JobSummaryComponent, StatusLabelPipe, MatFormField, MatInput, MatLabel, MatButton, FormsModule, ReactiveFormsModule, MatHint, StatusIconComponent],
  providers: [{ provide: MatPaginatorIntl, useClass: CustomPaginatorIntl }],
  templateUrl: './jobs.component.html',
  styleUrl: './jobs.component.scss'
})
export class JobsComponent implements OnInit {

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

  ngOnInit(): void {
    this.jobs$ = this.jobService.getAllJobs(this.currentPage, this.currentPageSize, this.currentStatus, this.filterLate, this.currentSort);
  }

  sortBy(sort: string): void {
    this.currentSort = sort;
    this.reloadJobs();
  }

  filterByStatus(status: String): void {
    // having two properties to handle status or "filter-late" filters may seem unclean, as they are exclusive at the moment
    // but it actually allow us to change our mind in the future and use both filters cumulatively 
    this.currentStatus = status === "filter-late" ? null : status as keyof typeof JobStatus;
    this.filterLate = status === "filter-late";
    this.reloadJobs();
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

  createJobWithUrl(): void {
    this.modalService.openCreateJobWithUrlModal((data: ComponentInputDomainData) => this.createJobWithMetadata(data.metadata.jobMetadata));
  }
  
  createJobWithMetadata(metadata: JobMetadata): void {
    this.modalService.openJobStepperModal(() => this.reloadJobs(), { jobMetadata: metadata });
  }

  createJob(): void {
    this.modalService.openJobStepperModal(() => this.reloadJobs());
  }

  onDelete(job: Job) :void {
    this.notificationService.confirmation($localize`:@@job.deleted:Job successfully deleted.`);
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