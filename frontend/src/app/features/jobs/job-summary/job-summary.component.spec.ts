import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JobSummaryComponent } from './job-summary.component';
import { JobService } from '@core/services/job.service';
import { NotificationService } from '@core/services/notification.service';
import { ModalService } from '@core/services/modal.service';
import { ConfirmDialogService } from '@core/services/confirm-dialog.service';
import { of } from 'rxjs';
import { Job } from '@core/model/job.interface';
import { ActivatedRoute } from '@angular/router';

describe('JobSummaryComponent', () => {
  let component: JobSummaryComponent;
  let fixture: ComponentFixture<JobSummaryComponent>;

  const jobServiceMock = {
    deleteJob: jest.fn()
  };

  const activatedRouteMock = {
    params: of({ id: 'job123' })
  };

  const notificationServiceMock = {
    confirmation: jest.fn()
  };

  const modalServiceMock = {
    openJobModal: jest.fn()
  };

  const confirmDialogServiceMock = {
    openConfirmDialog: jest.fn()
  };

  const fakeJob: Job = {
    id: '123',
    title: 'Test Job',
    url: "https://www.example.com",
    company: "Company",
    profile: "Profile",
    comment: "Comment",
    description: '',
    status: 'PENDING',
    salary: "Salary",
    rating: {value: 5},
    statusUpdatedAt:  new Date().toISOString();
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    activities: [],
    attachments: []
  } as Job;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobSummaryComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: JobService, useValue: jobServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock },
        { provide: ModalService, useValue: modalServiceMock },
        { provide: ConfirmDialogService, useValue: confirmDialogServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(JobSummaryComponent);
    component = fixture.componentInstance;
    component.job = fakeJob;
    fixture.detectChanges();

    jest.clearAllMocks();
  });

  it('should emit jobChanged and update local job on onJobChanged()', () => {
    const newJob = { ...fakeJob, title: 'Updated Title' };
    const spy = jest.spyOn(component.jobChanged, 'emit');

    component.onJobChanged(newJob);

    expect(component.job.title).toBe('Updated Title');
    expect(spy).toHaveBeenCalledWith(newJob);
  });

  it('should call modalService.openJobModal on editJob()', () => {
    const event = new MouseEvent('click');
    component.editJob(event, fakeJob);

    expect(modalServiceMock.openJobModal).toHaveBeenCalledWith('job', fakeJob, expect.any(Function));
  });

  it('should call confirmDialogService with job title on deleteJob()', () => {
    component.deleteJob(fakeJob);

    expect(confirmDialogServiceMock.openConfirmDialog).toHaveBeenCalled();
    const [message, callback] = confirmDialogServiceMock.openConfirmDialog.mock.calls[0];
    expect(message).toContain(fakeJob.title);
    expect(typeof callback).toBe('function');
  });

  it('should call jobService.deleteJob and emit deleted on confirmDeleteJob()', () => {
    jobServiceMock.deleteJob.mockReturnValueOnce(of(void 0));
    const spy = jest.spyOn(component.deleted, 'emit');

    component.confirmDeleteJob(fakeJob);

    expect(jobServiceMock.deleteJob).toHaveBeenCalledWith(fakeJob.id);
    expect(spy).toHaveBeenCalledWith(fakeJob);
  });

  it('should stop propagation and open attachment modal on manageAttachments()', () => {
    const event = { stopPropagation: jest.fn() } as unknown as MouseEvent;

    component.manageAttachments(event, fakeJob);

    expect(event.stopPropagation).toHaveBeenCalled();
    expect(modalServiceMock.openJobModal).toHaveBeenCalledWith('attachments', fakeJob, expect.any(Function));
  });
});