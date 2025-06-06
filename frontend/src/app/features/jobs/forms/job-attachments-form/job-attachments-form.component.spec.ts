import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { JobAttachmentsFormComponent } from './job-attachments-form.component';
import { JobService } from '@core/services/job.service';
import { NotificationService } from '@core/services/notification.service';
import { ErrorProcessorService } from '@core/services/error-processor.service';
import { Job } from '@core/model/job.interface';

describe('JobAttachmentsFormComponent', () => {
  let component: JobAttachmentsFormComponent;
  let fixture: ComponentFixture<JobAttachmentsFormComponent>;

  const jobMock: Job = { id: '123', title: 'Job title' } as Job;
  const jobServiceMock = {
    createAttachments: jest.fn()
  };
  const notificationServiceMock = {
    confirmation: jest.fn()
  };
  const errorProcessorServiceMock = {
    processError: jest.fn().mockReturnValue(of(null))
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: JobService, useValue: jobServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock },
        { provide: ErrorProcessorService, useValue: errorProcessorServiceMock }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(JobAttachmentsFormComponent);
    component = fixture.componentInstance;
    component.job = jobMock;
    component.defaultAttachements = 1;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with one attachment if defaultAttachements=1', () => {
    expect(component.attachments.length).toBe(1);
  });

  it('should add and remove attachment', () => {
    component.addAttachment();
    expect(component.attachments.length).toBe(2);
    component.removeAttachment(0);
    expect(component.attachments.length).toBe(1);
  });

  it('should process valid file in onFileChange', () => {
    const file = new File(['content'], 'test.txt', { type: 'text/plain' });
    const event = {
      target: {
        files: [file]
      }
    } as unknown as Event;

    const patchValueSpy = jest.spyOn(component.attachments.at(0), 'patchValue');
    component.onFileChange(event, 0);

    const reader = new FileReader();
    const loadHandler = jest.fn();
    Object.defineProperty(reader, 'onload', { set: loadHandler });

    reader.onload = () => {
      expect(patchValueSpy).toHaveBeenCalled();
    };
  });

  it('should show alert if file too big', () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});
    const file = new File(['content'], 'big.txt');
    Object.defineProperty(file, 'size', { value: component.maxFileSize + 1 });
    const event = {
      target: {
        files: [file],
        value: 'fake-path'
      }
    } as unknown as Event;

    component.onFileChange(event, 0);
    expect(alertSpy).toHaveBeenCalledWith('File too big');
  });

  it('should call jobService and emit on submit success', () => {
    const emitSpy = jest.spyOn(component.attachmentsSaved, 'emit');
    const jobResponse = { id: '123', title: 'Job title' } as Job;

    const attachment = component.attachments.at(0) as FormGroup;

    // remove controle, as we cannot patch it cleanly (input of type file cannot be patched with a value)
    attachment.removeControl('file');
    
    attachment.patchValue({
      name: 'Test file',
      filename: 'file.txt',
      content: 'base64content',
      fileU: 'chosen'
    });

    jobServiceMock.createAttachments.mockReturnValue(of(jobResponse));

    component.submit();

    expect(jobServiceMock.createAttachments).toHaveBeenCalled();
    expect(notificationServiceMock.confirmation).toHaveBeenCalled();
    expect(emitSpy).toHaveBeenCalledWith(jobResponse);
  });

  it('should handle error during submit', () => {
    const error = { message: 'fail' };
    jobServiceMock.createAttachments.mockReturnValue(throwError(() => error));
    const attachment = component.attachments.at(0) as FormGroup;

    // remove controle, as we cannot patch it cleanly (input of type file cannot be patched with a value)
    attachment.removeControl('file');

    attachment.patchValue({
      name: 'Test file',
      filename: 'file.txt',
      content: 'base64content',
      fileU: 'chosen'
    });

    component.submit();

    expect(jobServiceMock.createAttachments).toHaveBeenCalled();
    expect(errorProcessorServiceMock.processError).toHaveBeenCalledWith(expect.objectContaining({
      message: expect.stringContaining('Attachments could not be created.')
    }));
  });
});
