import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JobsComponent } from './jobs.component';
import { UserService } from '../../../core/services/user.service';
import { JobService } from '../../../core/services/job.service';
import { JobsListOptionsService } from '@core/services/jobs-list-options.service';
import { ModalService } from '../../../core/services/modal.service';
import { NotificationService } from '../../../core/services/notification.service';
import { of, Subject } from 'rxjs';
import { JobStatus } from '../../../core/model/job.interface';
import { PageEvent } from '@angular/material/paginator';
import { JobsListOptions } from '@app/core/model/jobs-list-options';

// Mocks basiques des services injectés
const userServiceMock = {
  getUser: jest.fn(),
  getUserSummary: jest.fn(),
  reloadUserSummary: jest.fn(),
};

const jobServiceMock = {
  getAllJobs: jest.fn(),
};

const jobsListOptionsServiceMock = {
  getJobsListOptions: jest.fn(),
  sort: jest.fn(),
  filter: jest.fn(),
  changePagination: jest.fn(),
};

const modalServiceMock = {
  openCreateJobWithUrlModal: jest.fn(),
  openJobStepperModal: jest.fn(),
  openJobModal: jest.fn(),
};

const notificationServiceMock = {
  confirmation: jest.fn(),
};

describe('JobsComponent', () => {
  let component: JobsComponent;
  let fixture: ComponentFixture<JobsComponent>;

  beforeEach(async () => {
    jest.clearAllMocks();

    // Mocks de retours
    userServiceMock.getUser.mockReturnValue(of({ id: 'user1' }));
    userServiceMock.getUserSummary.mockReturnValue(() => null); // Signal returning null initially
    jobsListOptionsServiceMock.getJobsListOptions.mockReturnValue(() => null); // Signal returning null initially
    jobServiceMock.getAllJobs.mockReturnValue(of({ content: [], totalElements: 0 }));

    await TestBed.configureTestingModule({
      imports: [JobsComponent],
      providers: [
        { provide: UserService, useValue: userServiceMock },
        { provide: JobService, useValue: jobServiceMock },
        { provide: JobsListOptionsService, useValue: jobsListOptionsServiceMock },
        { provide: ModalService, useValue: modalServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(JobsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize statusKeys from JobStatus enum keys', () => {
    expect(component.statusKeys).toEqual(Object.keys(JobStatus));
  });

  it('should call sort on jobsListOptionsService when sortBy called', () => {
    component.sortBy('someSort');
    expect(jobsListOptionsServiceMock.sort).toHaveBeenCalledWith('someSort');
  });

  it('should call filter on jobsListOptionsService when filter called', () => {
    component.filter('status', 'statusMeta');
    expect(jobsListOptionsServiceMock.filter).toHaveBeenCalledWith('status', 'statusMeta');
  });

  it('should call changePagination on jobsListOptionsService when handlePageEvent called', () => {
    const pageEvent: PageEvent = { pageIndex: 2, pageSize: 10, length: 100, previousPageIndex: 1 };
    component.handlePageEvent(pageEvent);
    expect(jobsListOptionsServiceMock.changePagination).toHaveBeenCalledWith(2, 10);
  });

  it('should reload jobs on onStatusChanged', () => {
    jest.spyOn(component, 'reloadJobs');
    component.onJobChanged({} as any);
    expect(component.reloadJobs).toHaveBeenCalled();
  });

  it('should call reloadUserSummary on reloadJobs', () => {
    component.reloadJobs();
    expect(userServiceMock.reloadUserSummary).toHaveBeenCalled();
  });

  it('should open create job with url modal', () => {
    component.createJobWithUrl();
    expect(modalServiceMock.openCreateJobWithUrlModal).toHaveBeenCalled();
  });

  it('should open job stepper modal with metadata', () => {
    const metadata = { jobMetadata: {} } as any;
    component.createJobWithMetadata(metadata);
    expect(modalServiceMock.openJobStepperModal).toHaveBeenCalledWith(expect.any(Function), { jobMetadata: metadata });
  });

  it('should open job stepper modal without metadata when createJob called', () => {
    component.createJob();
    expect(modalServiceMock.openJobStepperModal).toHaveBeenCalledWith(expect.any(Function));
  });

  it('should reload jobs and show confirmation on delete', () => {
    const job = {} as any;
    jest.spyOn(component, 'reloadJobs');
    component.onDelete(job);
    expect(component.reloadJobs).toHaveBeenCalled();
    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith('Job successfully deleted.');
  });

  it('should stop propagation and open job modal on manageAttachments', () => {
    const event = { stopPropagation: jest.fn() } as any as Event;
    const job = {} as any;
    component.manageAttachments(event, job);
    expect(event.stopPropagation).toHaveBeenCalled();
    expect(modalServiceMock.openJobModal).toHaveBeenCalledWith('attachments', job, expect.any(Function));
  });

  it('should update jobs$ observable when jobsOptions signal changes', async () => {
    // Mock getJobsListOptions
    const fakeSignal = jest.fn();
    const options = new JobsListOptions();
    options.changePagination(1, 10);
    fakeSignal.mockReturnValue(options);
    jobsListOptionsServiceMock.getJobsListOptions.mockReturnValue(fakeSignal);

    // re-instanciate component with new mock
    const fixture = TestBed.createComponent(JobsComponent);
    const comp = fixture.componentInstance;
    fixture.detectChanges();

    // fake new Options
    fakeSignal.mockImplementation(() => ({ page: 1, size: 10 }));

    // mock job service return value
    jobServiceMock.getAllJobs.mockReturnValue(of({ content: [{ id: 'job1' }], totalElements: 1 }));

    // manually trigger the fake signal
    comp["jobsOptions"]();
  

    expect(jobServiceMock.getAllJobs).toHaveBeenCalledWith(
      {
      "currentSort": "createdAt,desc",
      "itemsPerPage": 10,
      "jobStatus": null,
      "jobStatusMeta": null,
      "mustReload": null,
      "page": 1
    } as unknown as JobsListOptions);
  });
});