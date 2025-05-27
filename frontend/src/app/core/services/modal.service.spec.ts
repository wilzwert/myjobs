import { TestBed } from '@angular/core/testing';
import { ModalService } from './modal.service';
import { MatDialog } from '@angular/material/dialog';
import { JobEditionComponent } from '@features/jobs/job-editIion/job-edition.component';
import { JobStepperComponent } from '@features/jobs/job-stepper/job-stepper.component';
import { PasswordFormComponent } from '@features/user/password-form/password-form.component';
import { UserEditComponent } from '@features/user/user-edit/user-edit.component';
import { ModalComponent } from '@layout/modal/modal.component';
import { Job } from '@core/model/job.interface';
import { User } from '@core/model/user.interface';
import { CreateJobWithUrlFormComponent } from '@features/jobs/create-job-with-url-form/create-job-with-url-form.component';

describe('ModalService', () => {
  let modalService: ModalService;
  let dialog: MatDialog;
  let dialogOpenSpy: jest.SpyInstance;

  beforeEach(() => {
    const matDialogMock = {
      open: jest.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        ModalService,
        { provide: MatDialog, useValue: matDialogMock },
      ]
    });

    modalService = TestBed.inject(ModalService);
    dialog = TestBed.inject(MatDialog);
    dialogOpenSpy = jest.spyOn(dialog, 'open');
  });

  afterEach(() => {
    jest.clearAllMocks(); // Clear mocks after each test
  });

  it('should open job creation form modal with correct data', () => {
    const mockSucceeded = jest.fn();
    
    modalService.openCreateJobWithUrlModal(mockSucceeded);

    expect(dialogOpenSpy).toHaveBeenCalledWith(ModalComponent, expect.objectContaining({
      data: expect.objectContaining({
        component: CreateJobWithUrlFormComponent,
        succeeded: mockSucceeded,
        data: expect.objectContaining({
        }),
      }),
    }));
  });


  it('should open job modal with correct data', () => {
    const mockJob: Job = { id: '1', title: 'Test Job' } as Job;
    const mockSucceeded = jest.fn();
    const metadata = { someMetaData: 'value' };

    modalService.openJobModal('job', mockJob, mockSucceeded, metadata);

    expect(dialogOpenSpy).toHaveBeenCalledWith(ModalComponent, expect.objectContaining({
      data: expect.objectContaining({
        component: JobEditionComponent,
        succeeded: mockSucceeded,
        data: expect.objectContaining({
          job: mockJob,
          metadata: { ...metadata, type: 'job' },
        }),
      }),
    }));
  });

  it('should open job stepper modal with correct data', () => {
    const mockSucceeded = jest.fn();
    const metadata = { step: 1 };

    modalService.openJobStepperModal(mockSucceeded, metadata);

    expect(dialogOpenSpy).toHaveBeenCalledWith(ModalComponent, expect.objectContaining({
      data: expect.objectContaining({
        component: JobStepperComponent,
        succeeded: mockSucceeded,
        data: expect.objectContaining({ metadata }),
      }),
    }));
  });

  it('should open password modal with correct data', () => {
    const mockSucceeded = jest.fn();

    modalService.openPasswordModal(mockSucceeded);

    expect(dialogOpenSpy).toHaveBeenCalledWith(ModalComponent, expect.objectContaining({
      data: expect.objectContaining({
        component: PasswordFormComponent,
        succeeded: mockSucceeded,
      }),
    }));
  });

  it('should open user edit modal with correct data', () => {
    const mockUser: User = { id: 'user1', name: 'John Doe' } as unknown as User;
    const mockSucceeded = jest.fn();

    modalService.openUserEditModal(mockUser, mockSucceeded);

    expect(dialogOpenSpy).toHaveBeenCalledWith(ModalComponent, expect.objectContaining({
      data: expect.objectContaining({
        component: UserEditComponent,
        succeeded: mockSucceeded,
        data: expect.objectContaining({ user: mockUser }),
      }),
    }));
  });
});
