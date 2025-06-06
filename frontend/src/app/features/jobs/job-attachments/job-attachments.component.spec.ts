import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { JobAttachmentsComponent } from './job-attachments.component';
import { of, throwError } from 'rxjs';
import { JobService } from '@core/services/job.service';
import { FileService } from '@core/services/file.service';
import { ConfirmDialogService } from '@core/services/confirm-dialog.service';
import { ModalService } from '@core/services/modal.service';
import { NotificationService } from '@core/services/notification.service';
import { Job } from '@core/model/job.interface';
import { Attachment } from '@core/model/attachment.interface';
import { ProtectedFile } from '@app/core/model/protected-file.interface';

describe('JobAttachmentsComponent', () => {
  let component: JobAttachmentsComponent;
  let fixture: ComponentFixture<JobAttachmentsComponent>;

  let jobServiceMock: jest.Mocked<JobService>;
  let fileServiceMock: jest.Mocked<FileService>;
  let confirmDialogServiceMock: jest.Mocked<ConfirmDialogService>;
  let modalServiceMock: jest.Mocked<ModalService>;
  let notificationServiceMock: jest.Mocked<NotificationService>;

  const dummyJob: Job = {
    id: 'job1',
    attachments: [{ id: 'att1', name: 'CV.pdf' }] as Attachment[]
  } as Job;

  beforeEach(async () => {
    Object.defineProperty(global.URL, 'createObjectURL', {
      writable: true,
      value: jest.fn(() => 'blob:fake-url')
    });

    Object.defineProperty(global.URL, 'revokeObjectURL', {
      writable: true,
      value: jest.fn()
    });

    
    jobServiceMock = {
      getProtectedFile: jest.fn(),
      deleteAttachment: jest.fn()
    } as unknown as jest.Mocked<JobService>;

    fileServiceMock = {
      downloadFile: jest.fn()
    } as unknown as jest.Mocked<FileService>;

    confirmDialogServiceMock = {
      openConfirmDialog: jest.fn()
    } as unknown as jest.Mocked<ConfirmDialogService>;

    modalServiceMock = {
      openJobModal: jest.fn()
    } as unknown as jest.Mocked<ModalService>;

    notificationServiceMock = {
      error: jest.fn(),
      confirmation: jest.fn()
    } as unknown as jest.Mocked<NotificationService>;

    await TestBed.configureTestingModule({
      imports: [JobAttachmentsComponent],
      providers: [
        { provide: JobService, useValue: jobServiceMock },
        { provide: FileService, useValue: fileServiceMock },
        { provide: ConfirmDialogService, useValue: confirmDialogServiceMock },
        { provide: ModalService, useValue: modalServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(JobAttachmentsComponent);
    component = fixture.componentInstance;
    component.job = dummyJob;
    fixture.detectChanges();
  });

  it('should initialize displayForm based on formMode', () => {
    component.formMode = 'inline';
    component.ngOnInit();
    expect(component['displayForm']).toBe(true);
  });
  
  it('should download an attachment successfully', () => {
    const fakeBlob = new Blob(['hello']);
    const attachment = dummyJob.attachments[0];

    const spyCreateObjectURL = jest.spyOn(global.URL, 'createObjectURL').mockReturnValue('blob:url');
    const spyOpen = jest.spyOn(window, 'open').mockImplementation(() => null);
    const spyRevoke = jest.spyOn(global.URL, 'revokeObjectURL').mockImplementation(() => {});

    jobServiceMock.getProtectedFile.mockReturnValue(of({ url: 'protected-url' } as ProtectedFile));
    fileServiceMock.downloadFile.mockReturnValue(of(fakeBlob));

    component.downloadAttachement(dummyJob, attachment);

    expect(jobServiceMock.getProtectedFile).toHaveBeenCalledWith('job1', 'att1');
    expect(fileServiceMock.downloadFile).toHaveBeenCalledWith('protected-url', true);
    expect(spyCreateObjectURL).toHaveBeenCalled();
    expect(spyOpen).toHaveBeenCalledWith('blob:url', '_blank');
    expect(spyRevoke).toHaveBeenCalledWith('blob:url');
  });

  it('should handle download errors gracefully', () => {
    jobServiceMock.getProtectedFile.mockReturnValue(throwError(() => new Error('fail')));

    component.downloadAttachement(dummyJob, dummyJob.attachments[0]);

    expect(notificationServiceMock.error).toHaveBeenCalledWith(expect.stringContaining('File download failed'), expect.any(Error));
  });

  it('should call confirm dialog for deletion', () => {
    const attachment = dummyJob.attachments[0];
    component.deleteAttachment(dummyJob, attachment);

    expect(confirmDialogServiceMock.openConfirmDialog).toHaveBeenCalledWith(
      `Delete attachment "${attachment.name}" ?`,
      expect.any(Function)
    );
  });

  it('should delete the attachment from job', fakeAsync(() => {
    const attachment = dummyJob.attachments[0];
    jobServiceMock.deleteAttachment.mockReturnValue(of(void 0));

    component.confirmDeleteAttachment(dummyJob, attachment);

    tick();

    expect(jobServiceMock.deleteAttachment).toHaveBeenCalledWith('job1', 'att1');
    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith(expect.stringContaining('Attachment deleted successfully'))
    expect(dummyJob.attachments.length).toBe(0);
  }));

  it('should emit attachmentsSaved in modal mode', () => {
    component.formMode = 'modal';
    const emitSpy = jest.spyOn(component.attachmentsSaved, 'emit');

    component.onAttachmentsSaved(dummyJob);
    expect(emitSpy).toHaveBeenCalledWith(dummyJob);
  });

  it('should display form in inline mode when adding attachment', () => {
    component.formMode = 'inline';
    component.addAttachment(dummyJob);
    expect(component['displayForm']).toBe(true);
  });

  it('should open modal in modal mode when adding attachment', () => {
    component.formMode = 'modal';
    component.addAttachment(dummyJob);
    expect(modalServiceMock.openJobModal).toHaveBeenCalledWith('attachments-form', dummyJob, expect.any(Function), { defaultAttachments: 1 });
  });

  it('should hide form when cancelForm is called', () => {
    component.cancelForm();
    expect(component['displayForm']).toBe(false);
  });
});
