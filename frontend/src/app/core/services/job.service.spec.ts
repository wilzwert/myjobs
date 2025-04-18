import { JobService } from './job.service';
import { DataService } from './data.service';
import { firstValueFrom, lastValueFrom, of } from 'rxjs';
import { Job, JobRating, JobStatus } from '../model/job.interface';
import { CreateJobRequest } from '../model/create-job-request.interface';
import { UpdateJobStatusRequest } from '../model/update-job-status-request.interface';
import { CreateJobAttachmentsRequest } from '../model/create-job-attachments-request.interface';
import { Page } from '../model/page.interface';
import { JobMetadata } from '../model/job-metadata.interface';
import { UpdateJobRequest } from '../model/update-job-request.interface';
import { CreateJobAttachmentRequest } from '../model/create-job-attachment-request.interface';
import { UpdateJobRatingRequest } from '../model/update-job-rating-request.interface';

describe('JobService', () => {
  let dataServiceMock: jest.Mocked<DataService>;
  let jobService: JobService;

  beforeEach(() => {
    dataServiceMock = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      patch: jest.fn(),
      delete: jest.fn()
    } as unknown as jest.Mocked<DataService>;

    jobService = new JobService(dataServiceMock);
  });

  it('current page and items per page should be -1 before any call', () => {
    expect(jobService.getCurrentPage()).toEqual(-1);
    expect(jobService.getItemsPerPage()).toEqual(-1);
  })

  it('getJobById should call dataService.get with job id', async () => {
    const job: Job = { id: '123' } as Job;
    dataServiceMock.get.mockReturnValue(of(job));

    const result = await firstValueFrom(jobService.getJobById('123'));
    expect(result).toEqual(job);
    expect(dataServiceMock.get).toHaveBeenCalledWith('jobs/123');
  });

  it('createJob should call post and reset jobsSubject', async () => {
    const request: CreateJobRequest = { title: 'New Job' } as CreateJobRequest;
    const createdJob: Job = { id: '1', title: 'New Job' } as Job;

    dataServiceMock.post.mockReturnValue(of(createdJob));

    const job = await firstValueFrom(jobService.createJob(request));
    expect(job).toEqual(createdJob);
    expect(dataServiceMock.post).toHaveBeenCalledWith('jobs', request);
  });

  it('deleteJob should call dataService.delete and reloadIfNecessary', async () => {
    const jobId = '123';
    dataServiceMock.delete.mockReturnValue(of(undefined));

    // Mock interne pour suivre reloadIfNecessary
    const reloadSpy = jest.spyOn<any, any>(jobService as any, 'reloadIfNecessary');
    (jobService as any).jobsSubject.next({
      content: [{ id: jobId }] as Job[],
      totalElementsCount: 1
    });

    const v = await firstValueFrom(jobService.deleteJob(jobId));
    expect(dataServiceMock.delete).toHaveBeenCalledWith('jobs/123');
    expect(reloadSpy).toHaveBeenCalled();
  });

  it('updateJob should call dataService.put and reloadIfNecessary', async () => {
    const jobId = '321';
    const job: Job = { id: jobId } as Job;
    const request: UpdateJobRequest = { title: 'New title' } as UpdateJobRequest;

    dataServiceMock.patch.mockReturnValue(of(job));

    const reloadSpy = jest.spyOn<any, any>(jobService as any, 'reloadIfNecessary');

    const result = await firstValueFrom(jobService.updateJob(jobId, request));
    expect(result).toEqual(job);
    expect(dataServiceMock.patch).toHaveBeenCalledWith(`jobs/${jobId}`, request);
    expect(reloadSpy).toHaveBeenCalledWith(job);
  });

  it('updateJobStatus should call dataService.put and reloadIfNecessary', () => {
    const jobId = '321';
    const job: Job = { id: jobId } as Job;
    const request: UpdateJobStatusRequest = { status: 'PENDING' } as UpdateJobStatusRequest;

    dataServiceMock.put.mockReturnValue(of(job));

    const reloadSpy = jest.spyOn<any, any>(jobService as any, 'reloadIfNecessary');

    jobService.updateJobStatus(jobId, request).subscribe(result => {
      expect(result).toEqual(job);
      expect(dataServiceMock.put).toHaveBeenCalledWith(`jobs/${jobId}/status`, request);
      expect(reloadSpy).toHaveBeenCalledWith(job);
    });
  });

  it('createAttachments should call createAttachment for each attachment', async () => {
    const jobId = '789';
    const job: Job = { id: jobId } as Job;
    const request: CreateJobAttachmentsRequest = {
      attachments: [{ name: 'cv.pdf' }, { name: 'motivation.pdf' }]
    } as CreateJobAttachmentsRequest;

    const createAttachmentSpy = jest
      .spyOn(jobService, 'createAttachment')
      .mockReturnValue(of(job));

    const result = await firstValueFrom(jobService.createAttachments(jobId, request));
    expect(createAttachmentSpy).toHaveBeenCalledTimes(2);
    expect(result).toEqual(job);
    
  });

  it('createAttachment should call dataService.post and reloadIfNecessary', async () => {
    const jobId = '789';
    const job: Job = { id: jobId } as Job;
    const request: CreateJobAttachmentRequest = { name: 'cv.pdf' } as CreateJobAttachmentRequest;

    dataServiceMock.post.mockReturnValue(of(job));

    const result = await firstValueFrom(jobService.createAttachment(jobId, request));
    expect(dataServiceMock.post).toHaveBeenCalledWith(`jobs/${jobId}/attachments`, request);
    expect(result).toEqual(job);
      
  });

  it('getAllJobs should call dataService.get if page changes', async () => {
    const page: Page<Job> = {
      content: [{ id: '1' }] as Job[],
      totalElementsCount: 1,
      currentPage: 1,
      pageSize: 1,
      pagesCount: 1
    };

    dataServiceMock.get.mockReturnValue(of(page));

    const result = await firstValueFrom(jobService.getAllJobs(1, 10, null, 'createdAt'));
    expect(result).toEqual(page);
    expect(dataServiceMock.get).toHaveBeenCalledWith('jobs?page=1&itemsPerPage=10&sort=createdAt');
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

    // first call (loading)
    const firstLoad = await firstValueFrom(jobService.getAllJobs(1, 10, null, 'createdAt'));
    // second call with same parameters
    const secondResult = await firstValueFrom(jobService.getAllJobs(1, 10, null, 'createdAt'));
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

  it('updateJobStatus should trigger job update in BehaviorSubject', async () => {
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
  
    dataServiceMock.put.mockReturnValue(of(updatedJob));

    const result = await lastValueFrom(jobService.updateJobStatus('job-1', { status: 'PENDING' } as UpdateJobStatusRequest));
    expect(result).toEqual(updatedJob);
    const updatedPage = (jobService as any).jobsSubject.value;
    expect(updatedPage.content[0]).toEqual(updatedJob);
    
  });

  it('updateJobRating should trigger job update in BehaviorSubject', async () => {
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
  
    dataServiceMock.put.mockReturnValue(of(updatedJob));

    const result = await lastValueFrom(jobService.updateJobRating('job-1', { rating: 3 } as UpdateJobRatingRequest));
    expect(result).toEqual(updatedJob);
    const updatedPage = (jobService as any).jobsSubject.value;
    expect(updatedPage.content[0]).toEqual(updatedJob);
    
  });

  it('deleteJob should trigger job deletion in BehaviorSubject', async () => {
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

    const result = await lastValueFrom(jobService.deleteJob('job-1'));
    expect(result).toEqual(void 0);
    const updatedPage = (jobService as any).jobsSubject.value;
    expect(updatedPage.content).toHaveLength(0);
  });


});