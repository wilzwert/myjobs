import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JobsComponent } from './jobs.component';
import { JobService } from '../../../core/services/job.service';
import { ModalService } from '../../../core/services/modal.service';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { NotificationService } from '../../../core/services/notification.service';
import { of } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { Job, JobStatus } from '../../../core/model/job.interface';
import { MatSelectChange } from '@angular/material/select';


describe('JobsComponent', () => {
  let component: JobsComponent;
  let fixture: ComponentFixture<JobsComponent>;
  let jobServiceMock: any;
  let modalServiceMock: any;
  let confirmDialogServiceMock: any;
  let notificationServiceMock: any;

  beforeEach(async () => {
    jobServiceMock = {
      getCurrentPage: jest.fn().mockReturnValue(0),
      getItemsPerPage: jest.fn().mockReturnValue(10),
      getAllJobs: jest.fn().mockReturnValue(of({ content: [], totalElements: 0 })),
      updateJobStatus: jest.fn().mockReturnValue(of({})),
      updateJobRating: jest.fn().mockReturnValue(of({})),
      deleteJob: jest.fn().mockReturnValue(of({})),
      getJobMetadata: jest.fn().mockReturnValue(of({}))
    };

    modalServiceMock = {
      openJobModal: jest.fn(),
      openJobStepperModal: jest.fn()
    };

    confirmDialogServiceMock = {
      openConfirmDialog: jest.fn()
    };

    notificationServiceMock = {
      confirmation: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [JobsComponent, ReactiveFormsModule, MatPaginatorModule],
      providers: [
        { provide: JobService, useValue: jobServiceMock },
        { provide: ModalService, useValue: modalServiceMock },
        { provide: ConfirmDialogService, useValue: confirmDialogServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(JobsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the urlForm and jobs$', () => {
    expect(component.urlForm).toBeDefined();
    expect(jobServiceMock.getAllJobs).toHaveBeenCalledWith(0, 10, null, 'createdAt,desc');
  });

  it('should call setStatus and reload jobs', () => {
    const spy = jest.spyOn(component, 'reloadJobs');
    const event = { value: JobStatus.PENDING } as MatSelectChange;
    component.setStatus(event);
    expect(component.currentStatus).toBe(JobStatus.PENDING);
    expect(spy).toHaveBeenCalled();
  });

  it('should update job status and show confirmation', () => {
    const job = { id: '1' } as Job;
    const event = { value: JobStatus.DONE } as MatSelectChange;
    component.updateJobStatus(job, event);
    expect(jobServiceMock.updateJobStatus).toHaveBeenCalledWith('1', { status: JobStatus.DONE });
    expect(notificationServiceMock.confirmation).toHaveBeenCalled();
  });

  it('should update job rating and show confirmation', () => {
    const job = { id: '1' } as Job;
    component.updateJobRating(job, 4.5);
    expect(jobServiceMock.updateJobRating).toHaveBeenCalledWith('1', { rating: 4.5 });
    expect(notificationServiceMock.confirmation).toHaveBeenCalled();
  });

  it('should handle pagination event', () => {
    const event = { pageIndex: 2, pageSize: 20 } as PageEvent;
    component.handlePageEvent(event);
    expect(component.currentPage).toBe(2);
    expect(component.currentPageSize).toBe(20);
    expect(jobServiceMock.getAllJobs).toHaveBeenCalledWith(2, 20, null, 'createdAt,desc');
  });

  it('should reload jobs', () => {
    component.reloadJobs();
    expect(jobServiceMock.getAllJobs).toHaveBeenCalledWith(0, 10, null, 'createdAt,desc');
  });

  it('should create job with metadata', () => {
    component.urlForm?.get('url')?.setValue('https://example.com');
    component.createJobWithMetadata();
    expect(jobServiceMock.getJobMetadata).toHaveBeenCalledWith('https://example.com');
    expect(modalServiceMock.openJobStepperModal).toHaveBeenCalled();
  });

  it('should create a job (without metadata)', () => {
    component.createJob();
    expect(modalServiceMock.openJobStepperModal).toHaveBeenCalled();
  });

  it('should edit job', () => {
    const job = { id: '123' } as Job;
    component.editJob(new Event('click'), job);
    expect(modalServiceMock.openJobModal).toHaveBeenCalledWith('job', job, expect.any(Function));
  });

  it('should delete job after confirmation', () => {
    const job = { id: '1', title: 'Test Job' } as Job;
    component.confirmDeleteJob(job);
    expect(jobServiceMock.deleteJob).toHaveBeenCalledWith('1');
    expect(notificationServiceMock.confirmation).toHaveBeenCalled();
  });

  it('should call confirm dialog before delete', () => {
    const job = { id: '1', title: 'Test Job' } as Job;
    component.deleteJob(job);
    expect(confirmDialogServiceMock.openConfirmDialog).toHaveBeenCalled();
  });

  it('should open attachments modal without routing', () => {
    const event = new Event('click');
    jest.spyOn(event, 'stopPropagation');
    const job = { id: '1' } as Job;
    component.manageAttachments(event, job);
    expect(event.stopPropagation).toHaveBeenCalled();
    expect(modalServiceMock.openJobModal).toHaveBeenCalledWith('attachments', job, expect.any(Function));
  });
});
