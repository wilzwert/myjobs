import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JobService } from '../../../core/services/job.service';
import { catchError, Observable, Subject, take, takeUntil, tap, throwError } from 'rxjs';
import { Job } from '../../../core/model/job.interface';
import { Title } from '@angular/platform-browser';
import { AsyncPipe, DatePipe } from '@angular/common';
import { ActivityType } from '../../../core/model/activity-type';
import { MatButton } from '@angular/material/button';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { MatCard, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle } from '@angular/material/card';
import { ModalService } from '../../../core/services/modal.service';
import { NotificationService } from '../../../core/services/notification.service';
import { ApiError } from '../../../core/errors/api-error';
import { RatingComponent } from '../rating/rating.component';
import { JobAttachmentsComponent } from '../job-attachments/job-attachments.component';
import { JobActivitiesComponent } from "../job-activities/job-activities.component";
import { StatusLabelPipe } from '../../../core/pipe/status-label.pipe';

@Component({
  selector: 'app-job-detail',
  imports: [AsyncPipe, DatePipe, StatusLabelPipe, MatCard, MatCardHeader, MatCardTitle, MatCardContent, MatCardSubtitle, MatButton, RatingComponent, JobAttachmentsComponent, JobActivitiesComponent],
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
        return throwError(() => error);
      })
    );
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
    this.confirmDialogService.openConfirmDialog($localize `:@@warning.job.delete:Delete job "${job.title}" ? All data will be lost.`, () => this.confirmDeleteJob(job));
  }

  updateJobRating(job: Job, rating: number) :void {
    this.jobService.updateJobRating(job.id, {rating: rating}).pipe(
      take(1),
      catchError((error: ApiError) => {
        return throwError(() => error);

      })
    ).subscribe(() => {
      this.notificationService.confirmation($localize `:@@info.job.rating.updated:Rating updated successfully.`);
      this.loadJob(job.id);
    });
  }
}
