import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import {MatCardModule} from '@angular/material/card';
import { Page } from '../../../core/model/page.interface';
import { Job, JobStatus } from '../../../core/model/job.interface';
import { JobService } from '../../../core/services/job.service';
import { RouterLink } from '@angular/router';
import { JobModalService } from '../../../core/services/job-modal.service';
import { StatusLabelPipe } from '../../../core/pipe/status-label.pipe';
import { MatFormField, MatLabel, MatOption, MatSelect, MatSelectChange } from '@angular/material/select';


@Component({
  selector: 'app-jobs',
  imports: [AsyncPipe, MatCardModule, MatPaginatorModule, RouterLink, StatusLabelPipe, MatFormField, MatLabel, MatSelect, MatOption],
  templateUrl: './jobs.component.html',
  styleUrl: './jobs.component.scss'
})
export class JobsComponent implements OnInit {

  public jobs$!: Observable<Page<Job>>;

  public currentPage: number;
  public currentPageSize: number;
  public currentStatus: JobStatus | null = null;
  statusKeys: string[] = [];

  constructor(private jobService: JobService, private jobModalService: JobModalService) {
    this.currentPage = jobService.getCurrentPage();
    if(this.currentPage == -1) {
      this.currentPage = 0;
    }
    this.currentPageSize = jobService.getItemsPerPage();
    if(this.currentPageSize == -1) {
      this.currentPageSize = 10;
    }

    this.statusKeys=Object.keys(JobStatus);
  }

  ngOnInit(): void {
    this.jobs$ = this.jobService.getAllJobs(this.currentPage, this.currentPageSize, this.currentStatus);
  }

  setStatus(event: MatSelectChange):void {
    this.currentStatus = event.value;
    this.reloadJobs();
  }

  handlePageEvent(event: PageEvent) {
    this.jobs$ = this.jobService.getAllJobs(event.pageIndex, event.pageSize, this.currentStatus);
    this.currentPage = event.pageIndex;
    this.currentPageSize = event.pageSize;
  }

  reloadJobs(job: Job | null = null): void {
    this.currentPage = 0;
    this.jobs$ = this.jobService.getAllJobs(this.currentPage, this.currentPageSize, this.currentStatus);

  }

  createJob(): void {
    this.jobModalService.openJobStepperModal(() => this.reloadJobs());
  }

  editJob(event: Event, job: Job): void {
    // prevent routing to job detail 
    event.stopPropagation();
    // don't reload list as the edited job is replaced after update by the service
    this.jobModalService.openJobModal('job', job, () => {});
  }

  editAttachments(event: Event, job: Job): void {
    // prevent routing to job detail 
    event.stopPropagation();
    // don't reload list as the edited job is replaced after update by the service
    this.jobModalService.openJobModal('attachments', job, () => {});
  }
}
