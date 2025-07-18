import { TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { JobsListOptionsService } from './jobs-list-options.service';
import { DataStorageService } from './data-storage.service';
import { UserService } from './user.service';
import { JobsListOptions } from '../model/jobs-list-options';
import { JobStatus, JobStatusMeta } from '../model/job.interface';
import { UserSummary } from '../model/user-summary.interface';

describe('JobsListOptionsService', () => {
  let service: JobsListOptionsService;

  // Mocks
  const setItem = jest.fn();
  const getItem = jest.fn();
  const dataStorageMock = {
    getItem,
    setItem,
  };

  let userSummarySignal = signal<UserSummary | null | false>(null);
 
  const userServiceMock = {
    getUserSummary: jest.fn(() => userSummarySignal),
  };

  beforeEach(() => {
    jest.clearAllMocks();

    TestBed.configureTestingModule({
      providers: [
        JobsListOptionsService,
        { provide: DataStorageService, useValue: dataStorageMock },
        { provide: UserService, useValue: userServiceMock },
      ],
    });

    service = TestBed.inject(JobsListOptionsService);
  });

  it('should initialize from stored options when summary unable to load', () => {
    const stored = new JobsListOptions();
    stored.changePagination(1, 15);
    stored.filter(JobStatus.PENDING, null);
    stored.sort("rating,asc");
    getItem.mockReturnValue(stored);

    // simulate a summary loading error
    userSummarySignal.set(false);
    TestBed.flushEffects();

    // check options have been retrieved from the storage service
    expect(getItem).toHaveBeenCalledWith('jobs-filter');
    expect(service.getCurrentOptions()).toBeInstanceOf(JobsListOptions);
    const currentOptions = service.getCurrentOptions();
    expect(currentOptions).toBeInstanceOf(JobsListOptions);
    expect(currentOptions.getStatus()).toEqual(JobStatus.PENDING);
    expect(currentOptions.getSort()).toEqual("rating,asc");
    expect(currentOptions.getCurrentPage()).toEqual(1);
    expect(currentOptions.getItemsPerPage()).toEqual(15);
    expect(dataStorageMock.setItem).toHaveBeenCalled(); // sauvegarde appelée
  });

  it('should create default options when no options stored and summary unable to load', () => {
    // no stored options
    getItem.mockReturnValue(null);

    // simulate a summary loading error
    userSummarySignal.set(false);
    TestBed.flushEffects();

    // check options have been retrieved from the storage service
    expect(getItem).toHaveBeenCalledWith('jobs-filter');
    expect(service.getCurrentOptions()).toBeInstanceOf(JobsListOptions);
    const currentOptions = service.getCurrentOptions();
    expect(currentOptions).toBeInstanceOf(JobsListOptions);
    expect(currentOptions.getStatus()).toEqual(null);
    expect(currentOptions.getCurrentPage()).toEqual(0);
    expect(currentOptions.getItemsPerPage()).toEqual(10);
    expect(currentOptions.getSort()).toEqual("createdAt,desc");
    expect(dataStorageMock.setItem).toHaveBeenCalled(); // sauvegarde appelée
  });
  
  it('should handle new summary on summary load', () => {
    const stored = new JobsListOptions();
    stored.changePagination(1, 15);
    stored.filter(JobStatus.PENDING, null);
    stored.sort("rating,asc");
    getItem.mockReturnValue(stored);

    // Changer le userSummary doit déclencher l'effect
    const summary: UserSummary = {
        jobsCount: 3,
        activeJobsCount: 2,
        inactiveJobsCount: 1,
        lateJobsCount: 0,
        jobStatuses: { [JobStatus.CREATED]: 1 },
        usableJobStatusMetas: [],
    };
    userSummarySignal.set(summary);
    TestBed.flushEffects();

    // check options have been retrieved from the storage service
    expect(getItem).toHaveBeenCalledWith('jobs-filter');
    const currentOptions = service.getCurrentOptions();
    expect(currentOptions).toBeInstanceOf(JobsListOptions);
    expect(currentOptions.getStatus()).toEqual(null);
    expect(currentOptions.getSort()).toEqual("rating,asc");
    expect(dataStorageMock.setItem).toHaveBeenCalled(); // sauvegarde appelée
  });
  
  it('should reset options on clear()', () => {
    service.clear();
    expect(service.getCurrentOptions()).toEqual(new JobsListOptions());
  });
  
  it('should apply status filter and save it', () => {
    const spy = jest.spyOn<any, any>(service as any, 'save');
    service['jobsListOptions'] = new JobsListOptions(); // internal init
    service.filter('PENDING', 'ACTIVE');
    expect(spy).toHaveBeenCalled();
    const newOptions = spy.mock.calls[0][0] as JobsListOptions;
    expect(newOptions.getStatus()).toBe('PENDING');
    // we expect null, as status and statusMeta cannot be used at the same time
    expect(newOptions.getStatusMeta()).toBe(null);
  });

  it('should apply statusMeta filter and save it', () => {
    const spy = jest.spyOn<any, any>(service as any, 'save');
    service['jobsListOptions'] = new JobsListOptions(); // internal init
    service.filter(null, 'ACTIVE');
    expect(spy).toHaveBeenCalled();
    const newOptions = spy.mock.calls[0][0] as JobsListOptions;
    expect(newOptions.getStatus()).toBe(null);
    expect(newOptions.getStatusMeta()).toBe('ACTIVE');
  });
  
  it('should remove invalid status from options based on UserSummary', () => {
    service['jobsListOptions'] = new JobsListOptions().filter('PENDING', null);
    const summary: UserSummary = {
        jobsCount: 3,
        activeJobsCount: 2,
        inactiveJobsCount: 1,
        lateJobsCount: 0,
        jobStatuses: {  }, // previous status should be removed
        usableJobStatusMetas: [],
    };
    const result = service.checkOptions(summary);
    expect(result).toBeInstanceOf(JobsListOptions);
    expect(result!.getStatus()).toBe(null);
  });
  
  it('should remove invalid statusMeta from options based on UserSummary', () => {
    service['jobsListOptions'] = new JobsListOptions().filter(null, 'ACTIVE');
    const summary: UserSummary = {
        jobsCount: 3,
        activeJobsCount: 2,
        inactiveJobsCount: 1,
        lateJobsCount: 0,
        jobStatuses: { [JobStatus.PENDING]: 2 },
        usableJobStatusMetas: [], // statusMeta non inclus
    };
    const result = service.checkOptions(summary);
    expect(result).toBeInstanceOf(JobsListOptions);
    expect(result!.getStatusMeta()).toBe(null);
  });

  
  it('should return null from checkOptions when options are compatible', () => {
    service['jobsListOptions'] = new JobsListOptions().filter('PENDING', 'ACTIVE');
    const summary: UserSummary = {
        jobsCount: 3,
        activeJobsCount: 2,
        inactiveJobsCount: 1,
        lateJobsCount: 0,
      jobStatuses: { [JobStatus.PENDING]: 2 },
      usableJobStatusMetas: [JobStatusMeta.ACTIVE],
    };
    const result = service.checkOptions(summary);
    expect(result).toBeNull();
  });
});
