import { Component, effect, OnDestroy, OnInit, Signal } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { AsyncPipe, KeyValuePipe } from '@angular/common';
import { MatPaginatorIntl, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';
import { Page } from '../../../core/model/page.interface';
import { Job, JobStatus } from '../../../core/model/job.interface';
import { JobService } from '../../../core/services/job.service';
import { ModalService } from '../../../core/services/modal.service';
import { StatusLabelPipe } from '../../../core/pipe/status-label.pipe';
import { MatButton } from '@angular/material/button';
import { NotificationService } from '../../../core/services/notification.service';
import { FormsModule, ReactiveFormsModule} from '@angular/forms';
import { MatRippleModule } from '@angular/material/core';
import { MatMenuModule } from '@angular/material/menu';
import { JobMetadata } from '../../../core/model/job-metadata.interface';
import { CustomPaginatorIntl } from '../../../core/services/custom-paginator-intl';
import { User } from '../../../core/model/user.interface';
import { UserService } from '../../../core/services/user.service';
import { MatIcon } from '@angular/material/icon';
import { JobSummaryComponent } from '../job-summary/job-summary.component';
import { ComponentInputDomainData } from '../../../core/model/component-input-data.interface';
import { UserSummary } from '@app/core/model/user-summary.interface';
import { StatusMetaLabelPipe } from '@app/core/pipe/status-filter-label.pipe';
import { JobsListOptions } from '@app/core/model/jobs-list-options';
import { JobsListOptionsService } from '@app/core/services/jobs-list-options.service';


@Component({
  selector: 'app-jobs',
  imports: [AsyncPipe, KeyValuePipe, MatMenuModule, MatRippleModule, MatCardModule, MatPaginatorModule, MatIcon, JobSummaryComponent, StatusLabelPipe, StatusMetaLabelPipe, MatButton, FormsModule, ReactiveFormsModule],
  providers: [{ provide: MatPaginatorIntl, useClass: CustomPaginatorIntl }],
  templateUrl: './jobs.component.html',
  styleUrl: './jobs.component.scss'
})
export class JobsComponent implements OnInit, OnDestroy {

  private destroy$: Subject<boolean> = new Subject<boolean>();

  statusKeys: string[] = [];

  public jobs$!: Observable<Page<Job>>;
  protected user$: Observable<User>;

  // make user's summary available to template
  protected userSummary!: Signal<UserSummary | null | false>;

  // make options available to the template
  protected jobsOptions!: Signal<JobsListOptions | null>;

  constructor(
    private userService: UserService, 
    private jobService: JobService, 
    private jobsListOptionsService: JobsListOptionsService, 
    private modalService: ModalService,
    private notificationService: NotificationService) {
    this.statusKeys = Object.keys(JobStatus);
    this.user$ = this.userService.getUser();

    // make summary available to template
    this.userSummary = userService.getUserSummary();
    this.jobsOptions = this.jobsListOptionsService.getJobsListOptions();
    effect(() => {
      const options = this.jobsOptions();
      if(options === null) {
        return;
      }
      this.jobs$ = this.jobService.getAllJobs(options);
    })
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
  }

  ngOnInit(): void {
  }

  sortBy(sort: string): void {
    this.jobsListOptionsService.sort(sort);
  }

  filter(status: string | null, statusMeta: string | null): void {
    this.jobsListOptionsService.filter(status, statusMeta);
  }

  handlePageEvent(event: PageEvent) {
    this.jobsListOptionsService.changePagination(event.pageIndex, event.pageSize);
  }

  onJobChanged(job: Job): void {
    this.reloadJobs();
  }

  reloadJobs(): void {
    // reloading user summary will trigger options and jobs loading
    this.userService.reloadUserSummary();
  }

  createJobWithUrl(): void {
    this.modalService.openCreateJobWithUrlModal((data: ComponentInputDomainData) => this.createJobWithMetadata(data.metadata.jobMetadata));
  }
  
  createJobWithMetadata(metadata: JobMetadata): void {
    this.modalService.openJobStepperModal(() => this.reloadJobs(), { jobMetadata: metadata });
  }

  createJob(): void {
    this.modalService.openJobStepperModal(() => {this.reloadJobs()});
  }

  onDelete(job: Job) :void {
    this.reloadJobs();
    this.notificationService.confirmation($localize`:@@job.deleted:Job successfully deleted.`);
  }
  
  manageAttachments(event: Event, job: Job): void {
    // prevent routing to job detail 
    event.stopPropagation();
    // don't reload list; as the edited job is replaced after update directly by the service
    this.modalService.openJobModal('attachments', job, () => { });
  }
}