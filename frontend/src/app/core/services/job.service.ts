import { Injectable } from '@angular/core';
import { DataService } from './data.service';
import { Job, JobStatus } from '../model/job.interface';
import { BehaviorSubject, forkJoin, map, Observable, of, switchMap } from 'rxjs';
import { Page } from '../model/page.interface';
import { CreateJobRequest } from '../model/create-job-request.interface';
import { UpdateJobRequest } from '../model/update-job-request.interface';
import { CreateJobAttachmentsRequest } from '../model/create-job-attachments-request.interface';
import { CreateJobAttachmentRequest } from '../model/create-job-attachment-request.interface';
import { CreateJobActivitiesRequest } from '../model/create-job-activities-request.interface';
import { CreateJobActivityRequest } from '../model/create-job-activity-request.interface';

@Injectable({
  providedIn: 'root'
})
export class JobService {

  private jobsSubject: BehaviorSubject<Page<Job> | null> = new BehaviorSubject<Page<Job> | null> (null);
  private currentPage: number = -1;
  private itemsPerPage: number = -1;
  private currentStatus: JobStatus | null = null;

  constructor(private dataService: DataService) { }

  getCurrentPage() :number {
    return this.currentPage
  }

  getItemsPerPage() :number {
    return this.itemsPerPage
  }


   /**
   * Retrieves the sorted jobs loded from the backend 
   * @returns the jobs
   */
  public getAllJobs(page: number, itemsPerPage: number, status: JobStatus | null = null): Observable<Page<Job>> {
    return this.jobsSubject.pipe(
      switchMap((jobsPage: Page<Job> | null) => {
        if(jobsPage === null || page != this.currentPage || status != this.currentStatus) {
          this.currentPage = page;
          this.currentStatus = status;
          this.itemsPerPage = itemsPerPage;
          return this.dataService.get<Page<Job>>(`jobs?page=${page}&itemsPerPage=${itemsPerPage}`+(status ? `&status=${status}` : '')).pipe(
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

  private reloadIfNecessary(j: Job) :void {
    const page: Page<Job> | null = this.jobsSubject.value;
    let existingJobIndex = -1
    if(page !== null) {
      existingJobIndex = page.content.findIndex((job: Job) => j.id == job.id);
      if(existingJobIndex !== -1) {
        page.content[existingJobIndex] = j;
      }
    }
    if(existingJobIndex === -1) {
      this.jobsSubject.next(null);
    }
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

  public createAttachment(jobId: string, request: CreateJobAttachmentRequest): Observable<Job> {
    return this.dataService.post<Job>(`jobs/${jobId}/attachments`, request).pipe(
      map((j: Job) => {
        this.reloadIfNecessary(j);
        return j;
      })
    );
  }

  public createAttachments(jobId: string, request: CreateJobAttachmentsRequest): Observable<Job> {
    const attachmentRequests = request.attachments.map(attachment => 
      this.createAttachment(jobId, attachment) // Appel de createAttachment pour chaque pièce jointe
    );

    return forkJoin(attachmentRequests).pipe(
        map((jobs: Job[]) => jobs[jobs.length - 1]) // Retourne le dernier Job mis à jour
    );
  }

  public deleteAttachment(jobId: string, attachmentId: string): Observable<void> {
    return this.dataService.delete<void>(`jobs/${jobId}/attachments/${attachmentId}`);
  }

  public createActivity(jobId: string, request: CreateJobActivityRequest): Observable<Job> {
    return this.dataService.post<Job>(`jobs/${jobId}/activities`, request).pipe(
      map((j: Job) => {
        this.reloadIfNecessary(j);
        return j;
      })
    );
  }

  public createActivities(jobId: string, request: CreateJobActivitiesRequest): Observable<Job> {
    const attachmentRequests = request.activities.map(activity => 
      this.createActivity(jobId, activity) // Appel de createAttachment pour chaque pièce jointe
    );

    return forkJoin(attachmentRequests).pipe(
        map((jobs: Job[]) => jobs[jobs.length - 1]) // Retourne le dernier Job mis à jour
    );
  }
}
