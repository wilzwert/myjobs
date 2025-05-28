import { ComponentFixture, TestBed } from '@angular/core/testing';
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

  let mockJobService: jest.Mocked<JobService>;
  let mockFileService: jest.Mocked<FileService>;
  let mockConfirmDialogService: jest.Mocked<ConfirmDialogService>;
  let mockModalService: jest.Mocked<ModalService>;
  let mockNotificationService: jest.Mocked<NotificationService>;

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

    
    mockJobService = {
      getProtectedFile: jest.fn(),
      deleteAttachment: jest.fn()
    } as any;

    mockFileService = {
      downloadFile: jest.fn()
    } as any;

    mockConfirmDialogService = {
      openConfirmDialog: jest.fn()
    } as any;

    mockModalService = {
      openJobModal: jest.fn()
    } as any;

    mockNotificationService = {
      error: jest.fn()
    } as any;

    await TestBed.configureTestingModule({
      imports: [JobAttachmentsComponent],
      providers: [
        { provide: JobService, useValue: mockJobService },
        { provide: FileService, useValue: mockFileService },
        { provide: ConfirmDialogService, useValue: mockConfirmDialogService },
        { provide: ModalService, useValue: mockModalService },
        { provide: NotificationService, useValue: mockNotificationService }
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

    mockJobService.getProtectedFile.mockReturnValue(of({ url: 'protected-url' } as ProtectedFile));
    mockFileService.downloadFile.mockReturnValue(of(fakeBlob));

    component.downloadAttachement(dummyJob, attachment);

    expect(mockJobService.getProtectedFile).toHaveBeenCalledWith('job1', 'att1');
    expect(mockFileService.downloadFile).toHaveBeenCalledWith('protected-url', true);
    expect(spyCreateObjectURL).toHaveBeenCalled();
    expect(spyOpen).toHaveBeenCalledWith('blob:url', '_blank');
    expect(spyRevoke).toHaveBeenCalledWith('blob:url');
  });

  it('should handle download errors gracefully', () => {
    mockJobService.getProtectedFile.mockReturnValue(throwError(() => new Error('fail')));

    component.downloadAttachement(dummyJob, dummyJob.attachments[0]);

    expect(mockNotificationService.error).toHaveBeenCalledWith(expect.stringContaining('File download failed.'), expect.any(Error));
  });

  it('should call confirm dialog for deletion', () => {
    const attachment = dummyJob.attachments[0];
    component.deleteAttachment(dummyJob, attachment);

    expect(mockConfirmDialogService.openConfirmDialog).toHaveBeenCalledWith(
      `Delete attachment "${attachment.name}" ?`,
      expect.any(Function)
    );
  });

  it('should delete the attachment from job', () => {
    const attachment = dummyJob.attachments[0];
    mockJobService.deleteAttachment.mockReturnValue(of(void 0));

    component.confirmDeleteAttachment(dummyJob, attachment);

    expect(mockJobService.deleteAttachment).toHaveBeenCalledWith('job1', 'att1');
    expect(dummyJob.attachments.length).toBe(0);
  });

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
    expect(mockModalService.openJobModal).toHaveBeenCalledWith('attachments-form', dummyJob, expect.any(Function), { defaultAttachments: 1 });
  });

  it('should hide form when cancelForm is called', () => {
    component.cancelForm();
    expect(component['displayForm']).toBe(false);
  });
});
