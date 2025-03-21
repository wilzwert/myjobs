import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JobService } from '../../../core/services/job.service';
import { catchError, Observable, Subject, takeUntil, tap, throwError } from 'rxjs';
import { Job } from '../../../core/model/job.interface';
import { Title } from '@angular/platform-browser';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-job-detail',
  imports: [AsyncPipe],
  templateUrl: './job-detail.component.html',
  styleUrl: './job-detail.component.scss'
})
export class JobDetailComponent implements OnInit, OnDestroy {

  private destroy$: Subject<boolean> = new Subject<boolean>();

  public job$!: Observable<Job>;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute, 
    private jobService: JobService, 
    private title: Title
  ) {}

  ngOnDestroy(): void {
    this.destroy$.next(true);
  }

  ngOnInit(): void {
    
    this.activatedRoute.params
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        this.job$ = this.jobService.getJobById(params['id']).pipe(
          // set page title once the job  is available
          tap((job: Job) =>{this.title.setTitle(`Job - ${job.title}`)}),
          catchError(() => {
            this.router.navigate(["/jobs"]);
            return throwError(() => new Error('Unable to load job'));
          })
        );
        // TODO : activity creation form this.initForm();
    });
  }

}
