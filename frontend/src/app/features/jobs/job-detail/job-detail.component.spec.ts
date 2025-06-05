import { of, Subject, takeUntil, throwError } from 'rxjs';
import { JobDetailComponent } from './job-detail.component';
import { Job } from '@core/model/job.interface';
import { fakeAsync } from '@angular/core/testing';
import { ApiError } from '@core/errors/api-error';
import { HttpErrorResponse } from '@angular/common/http';

// Mocks
const mockJob: Job = {
  id: 'job123',
  title: 'Test Job',
  // Ajoute les champs nécessaires selon ton interface
} as Job;

describe('JobDetailComponent', () => {
  let component: JobDetailComponent;

  const jobServiceMock = {
    getJobById: jest.fn(),
    deleteJob: jest.fn(),
    updateJobRating: jest.fn()
  };

  const confirmDialogServiceMock = {
    openConfirmDialog: jest.fn()
  };

  const modalServiceMock = {
    openJobModal: jest.fn()
  };

  const notificationServiceMock = {
    confirmation: jest.fn()
  };

  const routerMock = {
    navigate: jest.fn()
  };

  const titleServiceMock = {
    setTitle: jest.fn()
  };

  const activatedRouteMock = {
    params: of({ id: 'job123' })
  };

  const errorProcessorServiceMock = {
    processError: jest.fn()
  }

  beforeEach(() => {
    jest.clearAllMocks();
    
    jobServiceMock.getJobById.mockReturnValue(of(mockJob));
    jobServiceMock.deleteJob.mockReturnValue(of(void 0));
    jobServiceMock.updateJobRating.mockReturnValue(of(void 0));

    jest.clearAllMocks();
    component = new JobDetailComponent(
      routerMock as any,
      activatedRouteMock as any,
      jobServiceMock as any,
      confirmDialogServiceMock as any,
      modalServiceMock as any,
      notificationServiceMock as any,
      titleServiceMock as any,
      errorProcessorServiceMock as any
    );
  });
  
  it('should load job on init and set title', () => {
    component.ngOnInit();
    expect(jobServiceMock.getJobById).toHaveBeenCalledWith('job123');
    expect(component.job).toEqual(mockJob);
    expect(titleServiceMock.setTitle).toHaveBeenCalledWith('Job - Test Job');
  });

  it('should handle error when job loading fails', () => {
    jobServiceMock.getJobById.mockReturnValueOnce(throwError(() => new ApiError({ message: 'error'} as unknown as HttpErrorResponse)));
    
    component.ngOnInit();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/jobs']);
  });
  
  it('should reload job when reloadJob is called', async () => {
    
    component.reloadJob(mockJob);

    expect(jobServiceMock.getJobById).toHaveBeenCalledWith('job123');
  });

  it('should call modalService.openJobModal when editJob is called', async () => {
    component.editJob(mockJob);

    expect(modalServiceMock.openJobModal).toHaveBeenCalledWith('job', mockJob, expect.any(Function));
  });

  it('should show notification and navigate to /jobs on job delete', async () => {
    component.onDelete(mockJob);
    expect(jobServiceMock.deleteJob).not.toHaveBeenCalledWith('job123');
    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith($localize`:@@job.deleted:Job successfully deleted.`);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/jobs']);
  });

  it('should delete job when confirmed', async () => {
    component.confirmDeleteJob(mockJob);
    expect(jobServiceMock.deleteJob).toHaveBeenCalledWith('job123');
    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith($localize`:@@job.deleted:Job successfully deleted.`);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/jobs']);
  });

  it('should open confirm dialog on deleteJob', async () => {
    component.deleteJob(mockJob);

    expect(confirmDialogServiceMock.openConfirmDialog).toHaveBeenCalledWith(
      `Delete job "${mockJob.title}" ? All data will be lost.`,
      expect.any(Function)
    );
  });

  it('should complete destroy$ on ngOnDestroy', (done) => {
    // test observable
    const testObservable = new Subject<boolean>();
    const emitted: boolean[] = [];

    // subscribe until destroyed
    testObservable.pipe(takeUntil(component['destroy$'])).subscribe({
      next: (val) => emitted.push(val),
      complete: () => {
        expect(emitted).toEqual([]);
        done();
      }
    });

    // Act : destroy component
    component.ngOnDestroy();

    testObservable.next(true);
  });

});