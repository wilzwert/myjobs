import { JobsComponent } from './jobs.component';
import { of } from 'rxjs';
import { Page } from '@core/model/page.interface';
import { Job } from '@core/model/job.interface';
import { JobMetadata } from '@core/model/job-metadata.interface';
import { JobService } from '@core/services/job.service';
import { UserService } from '@core/services/user.service';
import { ModalService } from '@core/services/modal.service';
import { ConfirmDialogService } from '@core/services/confirm-dialog.service';
import { NotificationService } from '@core/services/notification.service';
import { User } from '@core/model/user.interface';

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
  } as unknown as jest.Mocked<JobService>;

  const userServiceMock = {
    getUser: jest.fn(),
  } as unknown as jest.Mocked<UserService>;

  const modalServiceMock = {
    openJobStepperModal: jest.fn(),
    openJobModal: jest.fn(),
  } as unknown as jest.Mocked<ModalService>;

  const confirmDialogServiceMock = {
    openConfirmDialog: jest.fn(),
  } as unknown as jest.Mocked<ConfirmDialogService>;

  const notificationServiceMock = {
    confirmation: jest.fn(),
  } as unknown as jest.Mocked<NotificationService>;

  beforeEach(() => {
    jest.clearAllMocks();

    jobServiceMock.getCurrentPage.mockReturnValue(0);
    jobServiceMock.getItemsPerPage.mockReturnValue(10);
    jobServiceMock.getAllJobs.mockReturnValue(of({ content: [], totalElementsCount: 0, pagesCount: 0 } as unknown as Page<Job>));
    userServiceMock.getUser.mockReturnValue(of({ id: 'user1', name: 'Test User' } as unknown as User));

    component = new JobsComponent(
      userServiceMock,
      jobServiceMock,
      modalServiceMock,
      confirmDialogServiceMock,
      notificationServiceMock,
    );

    component.ngOnInit();
  });

  it('should load jobs on init', () => {
    expect(jobServiceMock.getAllJobs).toHaveBeenCalledWith(0, 10, null, false, 'createdAt,desc');
  });

  it('should set status filter and reload jobs', () => {
    jobServiceMock.getAllJobs.mockClear();

    component.filterByStatus('OPEN');
    expect(component.currentStatus).toBe('OPEN');
    expect(component.filterLate).toBe(false);
    expect(jobServiceMock.getAllJobs).toHaveBeenCalled();

    component.filterByStatus('filter-late');
    expect(component.currentStatus).toBeNull();
    expect(component.filterLate).toBe(true);
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
    const fakeMetadata = { title: 'Job from URL' } as any as JobMetadata;
    jobServiceMock.getJobMetadata.mockReturnValue(of(fakeMetadata));
    
    component.createJobWithMetadata(fakeMetadata);

    setTimeout(() => {
      expect(modalServiceMock.openJobStepperModal).toHaveBeenCalledWith(expect.any(Function), { jobMetadata: fakeMetadata });
      done();
    }, 0);
  });

  it('should create job and open modal without metadata', () => {
    component.createJob();
    expect(modalServiceMock.openJobStepperModal).toHaveBeenCalledWith(expect.any(Function));
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
