import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JobFormComponent } from './job-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { JobService } from '@core/services/job.service';
import { NotificationService } from '@core/services/notification.service';
import { of, throwError } from 'rxjs';
import { Job } from '@core/model/job.interface';
import { TINYMCE_SCRIPT_SRC } from '@tinymce/tinymce-angular';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('JobFormComponent', () => {
  let component: JobFormComponent;
  let fixture: ComponentFixture<JobFormComponent>;
  let jobServiceMock: jest.Mocked<JobService>;
  let notificationServiceMock: jest.Mocked<NotificationService>;

  beforeEach(async () => {
    jobServiceMock = {
      createJob: jest.fn(),
      updateJob: jest.fn()
    } as any;

    notificationServiceMock = {
      confirmation: jest.fn()
    } as any;

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, JobFormComponent],
      providers: [
        { provide: JobService, useValue: jobServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock },
        { provide: TINYMCE_SCRIPT_SRC, useValue: 'tinymce/tinymce.min.js' }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA] // ignore subcomponents like EditorComponent
    }).compileComponents();

    fixture = TestBed.createComponent(JobFormComponent);
    component = fixture.componentInstance;
    component.jobMetadata = {
      url: 'https://example.com',
      title: 'Frontend Developer',
      company: 'Test Corp',
      description: 'Job description here',
      profile: 'Profile text',
      salary: '50k€'
    };
    fixture.detectChanges();
  });

  it('should create the form with job metadata', () => {
    expect(component.form).toBeDefined();
    expect(component.form?.value.title).toBe('Frontend Developer');
  });

  it('should call createJob and emit job on submit when no job is set', () => {
    const mockJob = { id: '1' } as Job;
    jobServiceMock.createJob.mockReturnValue(of(mockJob));
    const emitSpy = jest.spyOn(component.jobSaved, 'emit');

    component.submit();

    expect(jobServiceMock.createJob).toHaveBeenCalled();
    expect(notificationServiceMock.confirmation).toHaveBeenCalled();
    expect(emitSpy).toHaveBeenCalledWith(mockJob);
  });

  it('should call updateJob when job is set', () => {
    const jobToUpdate = { id: '42' } as Job;
    component.job = jobToUpdate;
    component.ngOnInit();
    fixture.detectChanges();
    const updatedJob = { ...jobToUpdate };
    jobServiceMock.updateJob.mockReturnValue(of(updatedJob));

    component.submit();

    expect(jobServiceMock.updateJob).toHaveBeenCalledWith('42', expect.anything());
    expect(notificationServiceMock.confirmation).toHaveBeenCalled();
  });

  it('should handle errors on submit', () => {
    const error = { message: 'Some error' };
    jobServiceMock.createJob.mockReturnValue(throwError(() => error));

    component.submit();

    expect(component.loading).toBe(false);
    expect(component.error).toContain('Job could not be');
  });

  it('should create a job from user input', () => {
    // given
    jobServiceMock.createJob.mockReturnValue(of({ id: '123' } as any));
    const emitSpy = jest.spyOn(component.jobSaved, 'emit');

    component.job = null;
    fixture.detectChanges();

    component.form?.setValue({
      url: 'https://test.com',
      title: 'Dev',
      company: 'Test Corp',
      description: 'Some description',
      profile: '',
      salary: ''
    });

    fixture.detectChanges();

    // when
    component.submit();

    // then
    expect(jobServiceMock.createJob).toHaveBeenCalledWith({
      url: 'https://test.com',
      title: 'Dev',
      company: 'Test Corp',
      description: 'Some description',
      profile: '',
      salary: ''
    });

    expect(emitSpy).toHaveBeenCalledWith({ id: '123' });
    expect(notificationServiceMock.confirmation).toHaveBeenCalled();
  });

  it('should show an error if jobService fails', () => {
    jobServiceMock.createJob.mockReturnValue(
      throwError(() => ({ message: 'Oups' }))
    );

    component.job = null;
    fixture.detectChanges();

    component.form?.setValue({
      url: 'https://test.com',
      title: 'Dev',
      company: 'Test Corp',
      description: 'Some description',
      profile: '',
      salary: ''
    });

    fixture.detectChanges();

    component.submit();

    expect(component.loading).toBe(false);
    expect(component.error).toContain('Job could not be created');
  });
});