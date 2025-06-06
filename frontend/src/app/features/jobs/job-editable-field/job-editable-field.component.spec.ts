import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JobEditableFieldComponent } from './job-editable-field.component';
import { ReactiveFormsModule, Validators } from '@angular/forms';
import { of } from 'rxjs';
import { JobService } from '@app/core/services/job.service';
import { NotificationService } from '@app/core/services/notification.service';
import { Job } from '@app/core/model/job.interface';

describe('JobEditableFieldComponent', () => {
  let component: JobEditableFieldComponent;
  let fixture: ComponentFixture<JobEditableFieldComponent>;
  let jobServiceMock: jest.Mocked<JobService>;
  let notificationServiceMock: jest.Mocked<NotificationService>;

  beforeEach(async () => {
    jobServiceMock = {
      updateJobField: jest.fn()
    } as unknown as jest.Mocked<JobService>;

    notificationServiceMock = {
      confirmation: jest.fn()
    } as unknown as jest.Mocked<NotificationService>;

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: JobService, useValue: jobServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(JobEditableFieldComponent);
    component = fixture.componentInstance;

    // Set required @Input()s
    component.job = { id: 'job1' } as Job;
    component.field = 'comment';
  });

  it('should call updateJobField and emit event on submit if form is valid', () => {
    const updatedJob = { id: 'job1', comment: 'Updated' } as Job;
    const emitSpy = jest.spyOn(component.fieldEdited, 'emit');
    jobServiceMock.updateJobField.mockReturnValue(of(updatedJob));

    component.form = component['fb'].group({ comment: ['Updated'] });

    component.submit();

    expect(component.loading).toBe(false);
    expect(component.formVisible).toBe(false);
    expect(jobServiceMock.updateJobField).toHaveBeenCalledWith('job1', { comment: 'Updated' });
    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith(expect.stringContaining('Job'));
    expect(emitSpy).toHaveBeenCalledWith(updatedJob);
  });

  it('should not call updateJobField if form is invalid', () => {
    component.form = component['fb'].group({ comment: ['', Validators.required] });
    component.form.markAsTouched();
    component.form.markAsDirty();
    jest.clearAllMocks();

    component.submit();

    expect(jobServiceMock.updateJobField).not.toHaveBeenCalled();
    expect(notificationServiceMock.confirmation).not.toHaveBeenCalled();
  });
});
