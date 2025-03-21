import { Injectable } from '@angular/core';
import { DataService } from './data.service';
import { Job } from '../model/job.interface';
import { BehaviorSubject, map, Observable, of, switchMap } from 'rxjs';
import { Page } from '../model/page.interface';
import { CreateJobRequest } from '../model/create-job-request.interface';
import { UpdateJobRequest } from '../model/update-job-request.interface';

@Injectable({
  providedIn: 'root'
})
export class JobService {

  private jobsSubject: BehaviorSubject<Page<Job> | null> = new BehaviorSubject<Page<Job> | null> (null);
  private currentPage: number = -1;
  private itemsPerPage: number = -1;

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
  public getAllJobs(page: number, itemsPerPage: number): Observable<Page<Job>> {
    return this.jobsSubject.pipe(
      switchMap((jobsPage: Page<Job> | null) => {
        if(jobsPage === null || page != this.currentPage) {
          this.currentPage = page;
          this.itemsPerPage = itemsPerPage;
          return this.dataService.get<Page<Job>>(`jobs?page=${page}&itemsPerPage=${itemsPerPage}`).pipe(
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
   * Updates a job
   * @returns the job
   */
   public updateJob(jobId: string, request: UpdateJobRequest): Observable<Job> {
    // using patch because only some fields are edited
    return this.dataService.patch<Job>(`jobs/${jobId}`, request).pipe(
      map((j: Job) => {
        const page: Page<Job> | null = this.jobsSubject.value;
        let existingJobIndex = -1
        if(page !== null) {
          existingJobIndex = page.content.findIndex((job: Job) => jobId == job.id);
          if(existingJobIndex !== -1) {
            page.content[existingJobIndex] = j;
          }
        }
        if(existingJobIndex === -1) {
          alert('reload');
          this.jobsSubject.next(null);
        }
        return j;
      })
    );
  }
}
