import { Injectable } from '@angular/core';
import { DataService } from './data.service';
import { Job } from '@core/model/job.interface';
import { BehaviorSubject, map, Observable, of, switchMap, tap } from 'rxjs';
import { Page } from '@core/model/page.interface';
import { CreateJobRequest } from '@core/model/create-job-request.interface';
import { UpdateJobFieldRequest, UpdateJobRequest } from '@core/model/update-job-request.interface';
import { CreateJobAttachmentsRequest } from '@core/model/create-job-attachments-request.interface';
import { CreateJobAttachmentRequest } from '@core/model/create-job-attachment-request.interface';
import { CreateJobActivitiesRequest } from '@core/model/create-job-activities-request.interface';
import { UpdateJobStatusRequest } from '@core/model/update-job-status-request.interface';
import { UpdateJobRatingRequest } from '@core/model/update-job-rating-request.interface';
import { JobMetadata } from '@core/model/job-metadata.interface';
import { SessionService } from './session.service';
import { ProtectedFile } from '../model/protected-file.interface';
import { JobsListOptions } from '../model/jobs-list-options';

@Injectable({
  providedIn: 'root'
})
export class JobService {

  private jobsSubject: BehaviorSubject<Page<Job> | null> = new BehaviorSubject<Page<Job> | null> (null);
  
  private currentOptions: JobsListOptions = new JobsListOptions();


  constructor(private dataService: DataService, private sessionService: SessionService) {
    
    this.sessionService.$isLogged().subscribe(l => {
        // reset subject when user logged in status changes
        this.jobsSubject.next(null);
      });
   }

   /**
   * Retrieves the sorted jobs loaded from the backend 
   * trying to avoid unncessary data service requests
   * @returns the jobs
   */
  public getAllJobs(jobsListOptions: JobsListOptions): Observable<Page<Job>> {
    return this.jobsSubject.pipe(
      switchMap((jobsPage: Page<Job> | null) => {
        if(jobsPage === null || jobsListOptions.getMustReload() || !this.currentOptions.equals(jobsListOptions)) {
          // create a new instance to store current options, otherwise the current instance would be the same as the one in the service
          this.currentOptions = jobsListOptions;
          this.currentOptions.forceReload(null);
          const status = this.currentOptions.getStatus();
          const statusMeta = this.currentOptions.getStatusMeta();
          let statusOrFilterParam = '';
          if(status !== null) {
            statusOrFilterParam += `status=${status}`;
          }
          else if (statusMeta !== null) {
            statusOrFilterParam += `statusMeta=${statusMeta}`;
          }
          return this.dataService.get<Page<Job>>(`jobs?page=${this.currentOptions.getCurrentPage()}&itemsPerPage=${this.currentOptions.getItemsPerPage()}`+(statusOrFilterParam ? `&${statusOrFilterParam}` : '')+`&sort=${this.currentOptions.getSort()}`).pipe(
            switchMap((fetchedJobs: Page<Job>) => {
              this.jobsSubject.next(fetchedJobs);
              return of(fetchedJobs);
            })
          )
        }
        return of(jobsPage);
      })
    );
  }

  /**
   * Retrieves a job by its id
   * @returns the job
   */
  public getJobById(jobId: string): Observable<Job> {
    return this.dataService.get<Job>(`jobs/${jobId}`);
  }

   /**
   * Creates a job
   * @returns the job
   */
   public createJob(request: CreateJobRequest): Observable<Job> {
    return this.dataService.post<Job>(`jobs`, request).pipe(
      map((j: Job) => {
        this.jobsSubject.next(null);
        return j;
      })
    );
  }

  /**
   * Retrieves a job by its id
   * @returns the job
   */
  public deleteJob(jobId: string): Observable<void> {
    return this.dataService.delete<void>(`jobs/${jobId}`).pipe(
      tap(() => {
        this.reloadIfNecessary({id: jobId} as Job, true);
      })
    );
  }
  
  private reloadIfNecessary(j: Job, remove: boolean = false) :void {
    const page: Page<Job> | null = this.jobsSubject.value;
    let existingJobIndex = -1
    if(page !== null) {
      existingJobIndex = page.content.findIndex((job: Job) => j.id == job.id);
      if(existingJobIndex !== -1) {
        if(remove) {
          page.content.splice(existingJobIndex, 1);
          page.totalElementsCount--;
        }
        else {
          page.content[existingJobIndex] = j;
        }
      }
    }

    if(existingJobIndex === -1) {
      this.jobsSubject.next(null);
    }
  }

  /**
   * Updates a job status
   * @returns the job
   */
  public updateJobStatus(jobId: string, request: UpdateJobStatusRequest): Observable<Job> {
    // using patch because only some fields are edited
    return this.dataService.patch<Job>(`jobs/${jobId}`, request).pipe(
      map((j: Job) => {
        this.reloadIfNecessary(j);
        return j;
      })
    );
  }

  /**
   * Updates a job status
   * @returns the job
   */
  public updateJobRating(jobId: string, request: UpdateJobRatingRequest): Observable<Job> {
    // using patch because only some fields are edited
    return this.dataService.patch<Job>(`jobs/${jobId}`, request).pipe(
      map((j: Job) => {
        this.reloadIfNecessary(j);
        return j;
      })
    );
  }

  
   /**
   * Updates a job
   * @returns the job
   */
   public updateJob(jobId: string, request: UpdateJobRequest): Observable<Job> {
    // using patch because only some fields are edited
    return this.dataService.patch<Job>(`jobs/${jobId}`, request).pipe(
      map((j: Job) => {
        this.reloadIfNecessary(j);
        return j;
      })
    );
  }

  public updateJobField(jobId: string, request: UpdateJobFieldRequest): Observable<Job> {
    // using patch because only some fields are edited
    return this.dataService.patch<Job>(`jobs/${jobId}`, request).pipe(
      map((j: Job) => {
        this.reloadIfNecessary(j);
        return j;
      })
    );
  }

  public createAttachment(jobId: string, request: CreateJobAttachmentRequest): Observable<Job> {
    return this.dataService.post<Job>(`jobs/${jobId}/attachments`, request).pipe(
      map((j: Job) => {
        this.reloadIfNecessary(j);
        return j;
      })
    );
  }

  public createAttachments(jobId: string, request: CreateJobAttachmentsRequest): Observable<Job> {
    return this.dataService.post<Job>(`jobs/${jobId}/attachments`, request.attachments).pipe(
      map((j: Job) => {
        this.reloadIfNecessary(j);
        return j;
      })
    );
  }

  public deleteAttachment(jobId: string, attachmentId: string): Observable<void> {
    return this.dataService.delete<void>(`jobs/${jobId}/attachments/${attachmentId}`);
  }

  public getProtectedFile(jobId: string, attachmentId: string): Observable<ProtectedFile> {
    return this.dataService.get<ProtectedFile>(`jobs/${jobId}/attachments/${attachmentId}/file/info`);
  }

  public createActivities(jobId: string, request: CreateJobActivitiesRequest): Observable<Job> {
    return this.dataService.post<Job>(`jobs/${jobId}/activities`, request.activities).pipe(
      map((j: Job) => {
        this.reloadIfNecessary(j);
        return j;
      })
    );
  }

  public getJobMetadata(url: string) :Observable<JobMetadata> {
    return this.dataService.get<JobMetadata>(`jobs/metadata?url=${url}`)
  } 
}