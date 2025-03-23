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
import { JobModalService } from '../../../core/services/job-modal.service';

@Component({
  selector: 'app-job-detail',
  imports: [AsyncPipe, ActivityLabelPipe, MatCard, MatCardHeader, MatCardTitle, MatCardContent, MatCardSubtitle, MatCardActions, MatButton],
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
    private jobModalService: JobModalService,
    private title: Title
  ) {}

  private loadJob(jobId: string): void {
    this.job$ = this.jobService.getJobById(jobId).pipe(
      // set page title once the job  is available
      tap((job: Job) =>{this.title.setTitle(`Job - ${job.title}`)}),
      catchError(() => {
        this.router.navigate(["/jobs"]);
        return throwError(() => new Error('Unable to load job'));
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

  deleteAttachment(job: Job, attachment: Attachment) :void {
    this.confirmDialogService.openConfirmDialog(`Delete attachment "${attachment.name}" ?`, () => this.confirmDeleteAttachment(job, attachment));
  }

  confirmDeleteAttachment(job: Job, attachment: Attachment) :void {
    this.jobService.deleteAttachment(job.id, attachment.id).pipe(
      take(1),
      tap(() => this.loadJob(job.id))
    ).subscribe();
  }

  downloadAttachement(job: Job, attachment: Attachment) :void {
    this.fileService.downloadFile(`/api/jobs/${job.id}/attachments/${attachment.id}/file`).subscribe((blob) => {
      const a = document.createElement('a');
      const objectUrl = URL.createObjectURL(blob);
      window.open(objectUrl, '_blank');
        /*
      console.log(objectUrl);
      const downloadFile = new File([blob], attachment.filename, { type: attachment.contentType }); 
      a.href = objectUrl;
      a.download = attachment.filename;
      a.click();*/
      URL.revokeObjectURL(objectUrl);
    });
  }

  addActivity(job: Job) :void {
    this.jobModalService.openJobModal('activity', job, () => this.loadJob(job.id))
  }
}
