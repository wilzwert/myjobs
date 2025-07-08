import { JobService } from './job.service';
import { DataService } from './data.service';
import { BehaviorSubject, firstValueFrom, lastValueFrom, of, Subject } from 'rxjs';
import { Job, JobRating, JobStatus, JobStatusMeta } from '@core/model/job.interface';
import { CreateJobRequest } from '@core/model/create-job-request.interface';
import { UpdateJobStatusRequest } from '@core/model/update-job-status-request.interface';
import { CreateJobAttachmentsRequest } from '@core/model/create-job-attachments-request.interface';
import { Page } from '@core/model/page.interface';
import { JobMetadata } from '@core/model/job-metadata.interface';
import { UpdateJobFieldRequest, UpdateJobRequest } from '@core/model/update-job-request.interface';
import { UpdateJobRatingRequest } from '@core/model/update-job-rating-request.interface';
import { SessionService } from './session.service';
import { JobsListOptions } from '../model/jobs-list-options';
import { CreateJobActivitiesRequest } from '../model/create-job-activities-request.interface';

describe('JobService', () => {
  let dataServiceMock: jest.Mocked<DataService>;
  let sessionServiceMock: jest.Mocked<SessionService>;
  let jobService: JobService;

  const isLoggedSubject = new BehaviorSubject<boolean>(true);

  beforeEach(() => {
    dataServiceMock = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      patch: jest.fn(),
      delete: jest.fn()
    } as unknown as jest.Mocked<DataService>;

    sessionServiceMock = {
      $isLogged: jest.fn(() => isLoggedSubject.asObservable()),
    } as unknown as jest.Mocked<SessionService>;

    jobService = new JobService(dataServiceMock, sessionServiceMock);
  });

  it('should reset jobsSubject when isLogged emits', done => {
    const jobsSubject = jobService["jobsSubject"] as BehaviorSubject<any>;

    // spy on jobsSubject next
    const nextSpy = jest.spyOn(jobsSubject, 'next');

    // trigger change logged status
    isLoggedSubject.next(true);

    setTimeout(() => {
      expect(nextSpy).toHaveBeenCalledWith(null);
      done();
    }, 0);
  });

  it('getJobById should call dataService.get with job id', (done) => {
    const job: Job = { id: '123' } as Job;
    dataServiceMock.get.mockReturnValue(of(job));

    jobService.getJobById('123').subscribe((result) => {
      expect(result).toEqual(job);
      expect(dataServiceMock.get).toHaveBeenCalledWith('jobs/123');
      done();
    });
  });

  it('createJob should call post and reset jobsSubject', (done) => {
    const request: CreateJobRequest = { title: 'New Job' } as CreateJobRequest;
    const createdJob: Job = { id: '1', title: 'New Job' } as Job;

    dataServiceMock.post.mockReturnValue(of(createdJob));

    jobService.createJob(request).subscribe((job) => {
      expect(job).toEqual(createdJob);
      expect((jobService as any).jobsSubject.value).toBeNull();
      expect(dataServiceMock.post).toHaveBeenCalledWith('jobs', request);
      done();
    });
  });

  it('deleteJob should call dataService.delete and reloadIfNecessary', (done) => {
    const jobId = '123';
    dataServiceMock.delete.mockReturnValue(of(undefined));

    //internal mock for reloadIfNecessary
    const reloadSpy = jest.spyOn<any, any>(jobService as any, 'reloadIfNecessary');
    (jobService as any).jobsSubject.next({
      content: [{ id: jobId }] as Job[],
      totalElementsCount: 1
    });

    jobService.deleteJob(jobId).subscribe(() => {
      expect(dataServiceMock.delete).toHaveBeenCalledWith('jobs/123');
      expect(reloadSpy).toHaveBeenCalled();
      done();
    })
  });

  it('updateJob should call dataService.patch and reloadIfNecessary', (done) => {
    const jobId = '321';
    const job: Job = { id: jobId } as Job;
    const request: UpdateJobRequest = { title: 'New title' } as UpdateJobRequest;

    dataServiceMock.patch.mockReturnValue(of(job));
    const reloadSpy = jest.spyOn<any, any>(jobService as any, 'reloadIfNecessary');

    jobService.updateJob(jobId, request).subscribe(result => {
      expect(result).toEqual(job);
      expect(dataServiceMock.patch).toHaveBeenCalledWith(`jobs/${jobId}`, request);
      expect(reloadSpy).toHaveBeenCalledWith(job);
      done();
    });
  });

  it('updateJobField should call dataService.patch and reloadIfNecessary', () => {
    const jobId = '321';
    const job: Job = { id: jobId } as Job;
    const request: UpdateJobFieldRequest = { title: 'Updateed title' } as UpdateJobFieldRequest;

    dataServiceMock.patch.mockReturnValue(of(job));

    const reloadSpy = jest.spyOn<any, any>(jobService as any, 'reloadIfNecessary');

    jobService.updateJobField(jobId, request).subscribe(result => {
      expect(result).toEqual(job);
      expect(dataServiceMock.patch).toHaveBeenCalledWith(`jobs/${jobId}`, request);
      expect(reloadSpy).toHaveBeenCalledWith(job);
    });
  });

  it('updateJobStatus should call dataService.patch and reloadIfNecessary', () => {
    const jobId = '321';
    const job: Job = { id: jobId } as Job;
    const request: UpdateJobStatusRequest = { status: 'PENDING' } as UpdateJobStatusRequest;

    dataServiceMock.patch.mockReturnValue(of(job));

    const reloadSpy = jest.spyOn<any, any>(jobService as any, 'reloadIfNecessary');

    jobService.updateJobStatus(jobId, request).subscribe(result => {
      expect(result).toEqual(job);
      expect(dataServiceMock.patch).toHaveBeenCalledWith(`jobs/${jobId}`, request);
      expect(reloadSpy).toHaveBeenCalledWith(job);
    });
  });

  it('updateJobRating should call dataService.patch and reloadIfNecessary', () => {
    const jobId = '321';
    const job: Job = { id: jobId } as Job;
    const request: UpdateJobRatingRequest = { rating: 3 } as UpdateJobRatingRequest;

    dataServiceMock.patch.mockReturnValue(of(job));

    const reloadSpy = jest.spyOn<any, any>(jobService as any, 'reloadIfNecessary');

    jobService.updateJobRating(jobId, request).subscribe(result => {
      expect(result).toEqual(job);
      expect(dataServiceMock.patch).toHaveBeenCalledWith(`jobs/${jobId}`, request);
      expect(reloadSpy).toHaveBeenCalledWith(job);
    });
  });

  it('should create attachments', (done) => {
    const jobId = '789';
    const job: Job = { id: jobId } as Job;
    const request: CreateJobAttachmentsRequest = {
      attachments: [
        { name: 'cv.pdf' }, { name: 'motivation.pdf' }
      ]
    } as CreateJobAttachmentsRequest;
    dataServiceMock.post.mockReturnValue(of(job));
    const reloadSpy = jest.spyOn<any, any>(jobService as any, 'reloadIfNecessary');
  jobService.createAttachments(jobId, request).subscribe(result => {
        expect(dataServiceMock.post).toHaveBeenCalledWith(`jobs/${jobId}/attachments`, request.attachments);
        expect(result).toEqual(job);
        expect(reloadSpy).toHaveBeenCalledWith(job);
        done()
      });
    }
  );

  it('should create activities', (done) => {
    const jobId = '789';
    const job: Job = { id: jobId } as Job;
    const request: CreateJobActivitiesRequest = {
      activities: [
        { type: 'RELAUNCH', comment: "Relaunched application" }, 
        { type: 'COMPANY_REFUSAL', comment: 'Company refused the application' }
      ]
    } as CreateJobActivitiesRequest;
    dataServiceMock.post.mockReturnValue(of(job));
    const reloadSpy = jest.spyOn<any, any>(jobService as any, 'reloadIfNecessary');
  jobService.createActivities(jobId, request).subscribe(result => {
        expect(dataServiceMock.post).toHaveBeenCalledWith(`jobs/${jobId}/activities`, request.activities);
        expect(result).toEqual(job);
        expect(reloadSpy).toHaveBeenCalledWith(job);
        done()
      });
    }
  );

  it('getAllJobs should call dataService.get if page changes', (done) => {
    const page: Page<Job> = {
      content: [{ id: '1' }] as Job[],
      totalElementsCount: 1,
      currentPage: 1,
      pageSize: 1,
      pagesCount: 1
    };

    dataServiceMock.get.mockReturnValue(of(page));

    const options = new JobsListOptions();
    options.changePagination(1, 10);

    jobService.getAllJobs(options).subscribe(result => {
      expect(result).toEqual(page);
      expect(dataServiceMock.get).toHaveBeenCalledWith('jobs?page=1&itemsPerPage=10&sort=createdAt,desc');
      done()
    });
  });

  it('getAllJobs should call dataService.get with status param', (done) => {
    const page: Page<Job> = {
      content: [{ id: '1' }] as Job[],
      totalElementsCount: 1,
      currentPage: 1,
      pageSize: 1,
      pagesCount: 1
    };

    dataServiceMock.get.mockReturnValue(of(page));

    const options = new JobsListOptions();
    options.filter(JobStatus.ACCEPTED, null);

    jobService.getAllJobs(options).subscribe(result => {
      expect(result).toEqual(page);
      expect(dataServiceMock.get).toHaveBeenCalledWith('jobs?page=0&itemsPerPage=10&status=ACCEPTED&sort=createdAt,desc');
      done()
    })
  });

  it('getAllJobs should call dataService.get with status param when both status and statusMeta set', (done) => {
    const page: Page<Job> = {
      content: [{ id: '1' }] as Job[],
      totalElementsCount: 1,
      currentPage: 1,
      pageSize: 1,
      pagesCount: 1
    };

    dataServiceMock.get.mockReturnValue(of(page));

    const options = new JobsListOptions();
    options.filter(JobStatus.ACCEPTED, JobStatusMeta.ACTIVE);

    jobService.getAllJobs(options).subscribe(result => {
      expect(result).toEqual(page);
      expect(dataServiceMock.get).toHaveBeenCalledWith('jobs?page=0&itemsPerPage=10&status=ACCEPTED&sort=createdAt,desc');
      done();
    });
    
  });


  it('getAllJobs should call dataService.get with statusMeta param', (done) => {
    const page: Page<Job> = {
      content: [{ id: '1' }] as Job[],
      totalElementsCount: 1,
      currentPage: 1,
      pageSize: 1,
      pagesCount: 1
    };

    dataServiceMock.get.mockReturnValue(of(page));

    const options = new JobsListOptions();
    options.filter(null, JobStatusMeta.ACTIVE);
    options.changePagination(1, 15);

    jobService.getAllJobs(options).subscribe(result => {
      expect(result).toEqual(page);
      expect(dataServiceMock.get).toHaveBeenCalledWith('jobs?page=1&itemsPerPage=15&statusMeta=ACTIVE&sort=createdAt,desc');
      done()
    })
  });

  it('getAllJobs should not call dataService.get if page/status/sort did not change', async () => {
    const page: Page<Job> = {
      content: [{ id: '1' }] as Job[],
      totalElementsCount: 1,
      currentPage: 1,
      pageSize: 1,
      pagesCount: 1
    };

    dataServiceMock.get.mockReturnValue(of(page));
    const options = new JobsListOptions();
    options.filter(null, JobStatusMeta.ACTIVE);
    options.changePagination(1, 15);

    // first call (loading)
    const firstLoad = await firstValueFrom(jobService.getAllJobs(options));
    // second call with same parameters
    const secondResult = await firstValueFrom(jobService.getAllJobs(options));
    expect(dataServiceMock.get).toHaveBeenCalledTimes(1); // only one call expected as page and order did not change
    expect(secondResult).toEqual(page);
  });

  

  it('getJobMetadata should call dataService.get with url', async () => {
    const url = 'https://example.com';
    const meta: JobMetadata = { title: 'from site' } as JobMetadata;
    dataServiceMock.get.mockReturnValue(of(meta));

    const result = await firstValueFrom(jobService.getJobMetadata(url));
    expect(result).toEqual(meta);
    expect(dataServiceMock.get).toHaveBeenCalledWith(`jobs/metadata?url=${url}`);
  });


  it('updateJob should trigger job update in BehaviorSubject', async () => {
    const initialJob: Job = { id: 'job-1', title: 'Initial' } as Job;
    const updatedJob: Job = { id: 'job-1', title: 'Updated' } as Job;
  
    const page: Page<Job> = {
      content: [initialJob],
      totalElementsCount: 1,
      currentPage: 1,
      pageSize: 1,
      pagesCount: 1
    };
  
    // inject an initial state into subject
    (jobService as any).jobsSubject.next(page);
  
    dataServiceMock.patch.mockReturnValue(of(updatedJob));

    const result = await lastValueFrom(jobService.updateJob('job-1', { title: 'Updated' } as UpdateJobRequest));
    expect(result).toEqual(updatedJob);
    const updatedPage = (jobService as any).jobsSubject.value;
    expect(updatedPage.content[0]).toEqual(updatedJob);
    
  });

  it('updateJobStatus should trigger job update in BehaviorSubject', (done) => {
    const initialJob: Job = { id: 'job-1', title: 'Initial', status: JobStatus.CREATED } as Job;
    const updatedJob: Job = { id: 'job-1', title: 'Updated', status: JobStatus.PENDING } as Job;
  
    const page: Page<Job> = {
      content: [initialJob],
      totalElementsCount: 1,
      currentPage: 1,
      pageSize: 1,
      pagesCount: 1
    };
  
    // inject an initial state into subject
    (jobService as any).jobsSubject.next(page);
  
    dataServiceMock.patch.mockReturnValue(of(updatedJob));

    jobService.updateJobStatus('job-1', { status: 'PENDING' } as UpdateJobStatusRequest).subscribe(result => {
      expect(result).toEqual(updatedJob);
      const updatedPage = (jobService as any).jobsSubject.value;
      expect(updatedPage.content[0]).toEqual(updatedJob);
      done()
    });
  });

  it('updateJobRating should trigger job update in BehaviorSubject', (done) => {
    const initialJob: Job = { id: 'job-1', title: 'Initial', rating: {value: 1} as JobRating} as Job;
    const updatedJob: Job = { id: 'job-1', title: 'Updated', rating: {value: 3} as JobRating } as Job;
  
    const page: Page<Job> = {
      content: [initialJob],
      totalElementsCount: 1,
      currentPage: 1,
      pageSize: 1,
      pagesCount: 1
    };
  
    // inject an initial state into subject
    (jobService as any).jobsSubject.next(page);
  
    dataServiceMock.patch.mockReturnValue(of(updatedJob));

    jobService.updateJobRating('job-1', { rating: 3 } as UpdateJobRatingRequest).subscribe(result => {
      expect(result).toEqual(updatedJob);
      const updatedPage = (jobService as any).jobsSubject.value;
      expect(updatedPage.content[0]).toEqual(updatedJob);
      done()
    });
    
  });

  it('deleteJob should trigger job deletion in BehaviorSubject', (done) => {
    const job: Job = { id: 'job-1', title: 'Job to delete' } as Job;  

    const page: Page<Job> = {
      content: [job],
      totalElementsCount: 1,
      currentPage: 1,
      pageSize: 1,
      pagesCount: 1
    };
  
    // inject an initial state into subject
    (jobService as any).jobsSubject.next(page);
  
    dataServiceMock.delete.mockReturnValue(of(void 0));

    jobService.deleteJob('job-1').subscribe(() => {
      const updatedPage = (jobService as any).jobsSubject.value;
      expect(updatedPage.content).toHaveLength(0);
      expect(updatedPage.totalElementsCount).toBe(0);
      done();
    })
  });
    
});