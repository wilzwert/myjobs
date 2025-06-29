import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { JobService } from '@core/services/job.service';
import { catchError, Subject, take, takeUntil, tap } from 'rxjs';
import { Job } from '@core/model/job.interface';
import { Title } from '@angular/platform-browser';
import { ActivityType } from '@core/model/activity-type';
import { MatButton, MatIconButton } from '@angular/material/button';
import { ConfirmDialogService } from '@core/services/confirm-dialog.service';
import { MatCardModule } from '@angular/material/card';
import { ModalService } from '@core/services/modal.service';
import { NotificationService } from '@core/services/notification.service';
import { ApiError } from '@core/errors/api-error';
import { JobAttachmentsComponent } from '@features/jobs/job-attachments/job-attachments.component';
import { JobActivitiesComponent } from "@features/jobs/job-activities/job-activities.component";
import { JobSummaryComponent } from '@features/jobs/job-summary/job-summary.component';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { ErrorProcessorService } from '@core/services/error-processor.service';
import { JobEditableFieldComponent } from '../job-editable-field/job-editable-field.component';

@Component({
  selector: 'app-job-detail',
  imports: [JobSummaryComponent, MatCardModule, MatButton, JobAttachmentsComponent, JobActivitiesComponent, MatButton, MatIconButton, MatIcon, MatTooltip, RouterLink, JobEditableFieldComponent],
  templateUrl: './job-detail.component.html',
  styleUrl: './job-detail.component.scss'
})
export class JobDetailComponent implements OnInit, OnDestroy {

  private destroy$: Subject<boolean> = new Subject<boolean>();

  public job!: Job;

  public ActivityType = ActivityType;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute, 
    private jobService: JobService,
    private confirmDialogService: ConfirmDialogService,
    private modalService: ModalService,
    private notificationService: NotificationService,
    private title: Title,
    private errorProcessorService: ErrorProcessorService
  ) {}

  private loadJob(jobId: string): void {
    this.jobService.getJobById(jobId).pipe(
      take(1),
      // set page title once the job  is available
      tap((job: Job) =>{
        this.title.setTitle(`Job - ${job.title}`);
      }),
      catchError((error: ApiError) => {
        this.router.navigate(["/jobs"]);
        return this.errorProcessorService.processError(error);
      })
    ).subscribe((job) => this.job = job);
  }

  reloadJob(job: Job) {
    this.loadJob(job.id);
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
  }

  ngOnInit(): void {
    this.activatedRoute.params
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        this.loadJob(params['id']);
    });
  }

  editJob(job: Job) :void {
    this.modalService.openJobModal('job', job, () => this.loadJob(job.id))
  }

  onJobChanged(job: Job): void {
    this.job = job;
  }

  onDelete(job: Job) :void {
    this.notificationService.confirmation($localize`:@@job.deleted:Job successfully deleted.`);
    this.router.navigate(["/jobs"]);
  }

  confirmDeleteJob(job: Job) :void {
    this.jobService.deleteJob(job.id).pipe(
      take(1),
      tap(() => {
        this.onDelete(job);
      })
    ).subscribe();
  }

  deleteJob(job: Job) :void {
    this.confirmDialogService.openConfirmDialog($localize `:@@warning.job.delete:Delete job "${job.title}" ? All data will be lost.`, () => this.confirmDeleteJob(job));
  }
}
