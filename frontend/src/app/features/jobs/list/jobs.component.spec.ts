import { JobsComponent } from './jobs.component';
import { of } from 'rxjs';
import { Page } from '../../../core/model/page.interface';
import { Job, JobStatus } from '../../../core/model/job.interface';

describe('JobsComponent', () => {
  let component: JobsComponent;

  // Mocks des services injectés
  const jobServiceMock = {
    getCurrentPage: jest.fn(),
    getItemsPerPage: jest.fn(),
    getAllJobs: jest.fn(),
    updateJobStatus: jest.fn(),
    updateJobRating: jest.fn(),
    getJobMetadata: jest.fn(),
    deleteJob: jest.fn(),
  };

  const userServiceMock = {
    getUser: jest.fn(),
  };

  const modalServiceMock = {
    openJobStepperModal: jest.fn(),
    openJobModal: jest.fn(),
  };

  const confirmDialogServiceMock = {
    openConfirmDialog: jest.fn(),
  };

  const notificationServiceMock = {
    confirmation: jest.fn(),
  };

  const fbMock = new (require('@angular/forms').FormBuilder)();

  beforeEach(() => {
    jest.clearAllMocks();

    jobServiceMock.getCurrentPage.mockReturnValue(0);
    jobServiceMock.getItemsPerPage.mockReturnValue(10);
    jobServiceMock.getAllJobs.mockReturnValue(of({ content: [], totalElementsCount: 0, pagesCount: 0 } as unknown as Page<Job>));
    userServiceMock.getUser.mockReturnValue(of({ id: 'user1', name: 'Test User' }));

    component = new JobsComponent(
      fbMock,
      userServiceMock as any,
      jobServiceMock as any,
      modalServiceMock as any,
      confirmDialogServiceMock as any,
      notificationServiceMock as any,
    );

    component.ngOnInit();
  });

  it('should create form with url control', () => {
    expect(component.urlForm).toBeDefined();
    expect(component.urlForm?.contains('url')).toBe(true);
  });

  it('should load jobs on init', () => {
    expect(jobServiceMock.getAllJobs).toHaveBeenCalledWith(0, 10, null, false, 'createdAt,desc');
  });

  it('should set status filter and reload jobs', () => {
    jobServiceMock.getAllJobs.mockClear();

    component.setStatus({ value: 'OPEN' } as any);
    expect(component.currentStatus).toBe('OPEN');
    expect(component.filterLate).toBe(false);
    expect(jobServiceMock.getAllJobs).toHaveBeenCalled();

    component.setStatus({ value: 'filter-late' } as any);
    expect(component.currentStatus).toBeNull();
    expect(component.filterLate).toBe(true);
  });

  it('should update job status and show notification', done => {
    jobServiceMock.updateJobStatus.mockReturnValue(of({ id: 'job1', status: JobStatus.RELAUNCHED }));

    const job = { id: 'job1', status: JobStatus.PENDING } as Job;

    component.updateJobStatus(job, { value: JobStatus.RELAUNCHED } as any);

    setTimeout(() => {
      expect(jobServiceMock.updateJobStatus).toHaveBeenCalledWith('job1', { status: JobStatus.RELAUNCHED } as any);
      expect(notificationServiceMock.confirmation).toHaveBeenCalledWith(expect.stringContaining('Status updated successfully'));
      done();
    }, 0);
  });

  it('should update job rating and show notification', done => {
    jobServiceMock.updateJobRating.mockReturnValue(of({ id: 'job1', rating: 4 }));

    const job = { id: 'job1', rating: 3 } as unknown as Job;

    component.updateJobRating(job, 4);

    setTimeout(() => {
      expect(jobServiceMock.updateJobRating).toHaveBeenCalledWith('job1', { rating: 4 });
      expect(notificationServiceMock.confirmation).toHaveBeenCalledWith(expect.stringContaining('Rating updated successfully'));
      done();
    }, 0);
  });

  it('should handle page event and load jobs', () => {
    jobServiceMock.getAllJobs.mockClear();

    component.handlePageEvent({ pageIndex: 2, pageSize: 20 } as any);

    expect(component.currentPage).toBe(2);
    expect(component.currentPageSize).toBe(20);
    expect(jobServiceMock.getAllJobs).toHaveBeenCalledWith(2, 20, component.currentStatus, component.filterLate, component.currentSort);
  });

  it('should reload jobs resetting page to 0', () => {
    jobServiceMock.getAllJobs.mockClear();

    component.currentPage = 5;

    component.reloadJobs();

    expect(component.currentPage).toBe(0);
    expect(jobServiceMock.getAllJobs).toHaveBeenCalledWith(0, component.currentPageSize, component.currentStatus, component.filterLate, component.currentSort);
  });

  it('should create job with metadata and open modal', done => {
    const fakeMetadata = { title: 'Job from URL' } as any;
    jobServiceMock.getJobMetadata.mockReturnValue(of(fakeMetadata));

    component.urlForm?.controls['url'].setValue('https://example.com/job');

    component.createJobWithMetadata();

    setTimeout(() => {
      expect(jobServiceMock.getJobMetadata).toHaveBeenCalledWith('https://example.com/job');
      expect(modalServiceMock.openJobStepperModal).toHaveBeenCalledWith(expect.any(Function), { jobMetadata: fakeMetadata });
      done();
    }, 0);
  });

  it('should create job and open modal without metadata', () => {
    component.createJob();
    expect(modalServiceMock.openJobStepperModal).toHaveBeenCalledWith(expect.any(Function));
  });

  it('should edit job and open modal', () => {
    const job = { id: 'job1' } as Job;
    const event = { stopPropagation: jest.fn() } as any;

    component.editJob(event, job);

    expect(modalServiceMock.openJobModal).toHaveBeenCalledWith('job', job, expect.any(Function));
  });

  it('should delete job after confirmation', done => {
    const job = { id: 'job1', title: 'Job to delete' } as Job;
    jobServiceMock.deleteJob.mockReturnValue(of(undefined));

    // simulate confirm dialog callback immediately calls the confirmDeleteJob
    confirmDialogServiceMock.openConfirmDialog.mockImplementation((msg, cb) => cb());

    component.deleteJob(job);

    setTimeout(() => {
      expect(confirmDialogServiceMock.openConfirmDialog).toHaveBeenCalledWith(expect.stringContaining(`Delete job "${job.title}"`), expect.any(Function));
      expect(jobServiceMock.deleteJob).toHaveBeenCalledWith(job.id);
      expect(notificationServiceMock.confirmation).toHaveBeenCalledWith(expect.stringContaining('Job successfully deleted'));
      done();
    }, 0);
  });

  it('should open attachments modal and stop event propagation', () => {
    const job = { id: 'job1' } as Job;
    const event = { stopPropagation: jest.fn() } as any;

    component.manageAttachments(event, job);

    expect(event.stopPropagation).toHaveBeenCalled();
    expect(modalServiceMock.openJobModal).toHaveBeenCalledWith('attachments', job, expect.any(Function));
  });

});
