import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JobService } from '../../../core/services/job.service';
import { catchError, Observable, Subject, take, takeUntil, tap, throwError } from 'rxjs';
import { Job } from '../../../core/model/job.interface';
import { Title } from '@angular/platform-browser';
import { AsyncPipe } from '@angular/common';
import { FileService } from '../../../core/services/file.service';
import { Attachment } from '../../../core/model/attachment.interface';
import { ActivityType } from '../../../core/model/activity-type';
import { ActivityLabelPipe } from '../../../core/pipe/activity-label.pipe';
import { MatButton } from '@angular/material/button';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle } from '@angular/material/card';
import { ModalService } from '../../../core/services/modal.service';
import { NotificationService } from '../../../core/services/notification.service';
import { ApiError } from '../../../core/errors/api-error';
import { RatingComponent } from '../rating/rating.component';
import { JobAttachmentsFormComponent } from '../job-attachments-form/job-attachments-form.component';
import { JobAttachmentsComponent } from '../job-attachments/job-attachments.component';

@Component({
  selector: 'app-job-detail',
  imports: [AsyncPipe, ActivityLabelPipe, MatCard, MatCardHeader, MatCardTitle, MatCardContent, MatCardSubtitle, MatCardActions, MatButton, RatingComponent, JobAttachmentsComponent, JobAttachmentsFormComponent],
  templateUrl: './job-detail.component.html',
  styleUrl: './job-detail.component.scss'
})
export class JobDetailComponent implements OnInit, OnDestroy {

  private destroy$: Subject<boolean> = new Subject<boolean>();

  public job$!: Observable<Job>;

  public ActivityType = ActivityType;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute, 
    private jobService: JobService, 
    private fileService: FileService,
    private confirmDialogService: ConfirmDialogService,
    private modalService: ModalService,
    private notificationService: NotificationService,
    private title: Title
  ) {}

  private loadJob(jobId: string): void {
    this.job$ = this.jobService.getJobById(jobId).pipe(
      // set page title once the job  is available
      tap((job: Job) =>{this.title.setTitle(`Job - ${job.title}`)}),
      catchError((error: ApiError) => {
        this.router.navigate(["/jobs"]);
        // set an explicit error message
        error.message = 'Unable to load job';
        return throwError(() => error);
      })
    );

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

  addActivity(job: Job) :void {
    this.modalService.openJobModal('activity', job, () => this.loadJob(job.id))
  }

  editJob(job: Job) :void {
    this.modalService.openJobModal('job', job, () => this.loadJob(job.id))
  }

  confirmDeleteJob(job: Job) :void {
    this.jobService.deleteJob(job.id).pipe(
      take(1),
      tap(() => {
        this.notificationService.confirmation("Job deleted successfully.");
        this.router.navigate(["/jobs"]);
      })
    ).subscribe();
  }

  deleteJob(job: Job) :void {
    this.confirmDialogService.openConfirmDialog(`Delete job "${job.title}" ? ALL DATA WILL BE LOST`, () => this.confirmDeleteJob(job));
  }

  updateJobRating(job: Job, rating: number) :void {
    this.jobService.updateJobRating(job.id, {rating: rating}).pipe(
      take(1),
      catchError((error: ApiError) => {
        this.notificationService.confirmation("Job rating update failed.");
        return throwError(() => error);

      })
    ).subscribe(() => {
      this.notificationService.confirmation("Job rating updated successfully.");
      this.loadJob(job.id);
    });
  }
}
