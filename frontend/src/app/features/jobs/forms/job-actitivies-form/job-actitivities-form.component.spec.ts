import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { JobActivitiesFormComponent } from './job-actitivities-form.component';
import { JobService } from '@core/services/job.service';
import { NotificationService } from '@core/services/notification.service';
import { ErrorProcessorService } from '@core/services/error-processor.service';
import { TranslatorService } from '@app/core/services/translator.service';
import { of, Subject, throwError } from 'rxjs';
import { Job } from '@app/core/model/job.interface';

describe('JobActivitiesFormComponent', () => {
  let component: JobActivitiesFormComponent;
  let fixture: ComponentFixture<JobActivitiesFormComponent>;

  let jobServiceMock: any;
  let notificationServiceMock: any;
  let errorProcessorServiceMock: any;
  let translatorServiceMock: any;

  beforeEach(async () => {
    jobServiceMock = {
      createActivities: jest.fn()
    };

    notificationServiceMock = {
      confirmation: jest.fn()
    };

    errorProcessorServiceMock = {
      processError: jest.fn(err => throwError(() => err))
    };

    translatorServiceMock = {
      translateError: jest.fn(msg => msg)
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: JobService, useValue: jobServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock },
        { provide: ErrorProcessorService, useValue: errorProcessorServiceMock },
        { provide: TranslatorService, useValue: translatorServiceMock },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(JobActivitiesFormComponent);
    component = fixture.componentInstance;

    // Injection de l’input Job obligatoire
    component.job = { id: 'job123', title: 'Test Job' } as any;
  });

  it('should create with default activities count', () => {
    component.defaultActivities = 2;
    component.ngOnInit();

    expect(component.activities.length).toBe(2);
    component.activities.controls.forEach(control => {
      expect(control.get('type')).toBeTruthy();
      expect(control.get('comment')).toBeTruthy();
    });
  });

  it('should add and remove activities', () => {
    component.ngOnInit();
    expect(component.activities.length).toBe(0);

    component.addActivity();
    expect(component.activities.length).toBe(1);

    component.removeActivity(0);
    expect(component.activities.length).toBe(0);
  });

  it('should submit valid form and emit event', () => {
    component.ngOnInit();
    component.addActivity();

    // fill form with valid values
    component.activities.at(0).setValue({ type: 'TYPE1', comment: 'A comment' });

    const jobResponse = { id: 'job123', title: 'Test Job Updated' } as Job;
    // use a subject to complete it after checking the component has been in a loading state
    const subject = new Subject<Job>();
    jobServiceMock.createActivities.mockReturnValue(subject.asObservable());

    const emitSpy = jest.spyOn(component.activitiesSaved, 'emit');

    component.submit();
    console.log('expecting loading');
    expect(component.loading).toBe(true);

    subject.next(jobResponse);
    subject.complete();
    

    expect(jobServiceMock.createActivities).toHaveBeenCalledWith('job123', {
      activities: [{ type: 'TYPE1', comment: 'A comment' }]
    });

    expect(component.loading).toBe(false);
    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith('Activity created successfully');
    expect(emitSpy).toHaveBeenCalledWith(jobResponse);
  });


  it('should handle error on submit', () => {
    component.ngOnInit();
    component.addActivity();

    component.activities.at(0).setValue({ type: 'TYPE1', comment: 'A comment' });

    const error = { message: 'error' };
    jobServiceMock.createActivities.mockReturnValue(throwError(() => error));
    errorProcessorServiceMock.processError.mockReturnValue(throwError(() => error));
    translatorServiceMock.translateError.mockReturnValue('translated error');

    component.submit();

    expect(component.loading).toBe(false);
    expect(error.message).toContain('Activities could not be created.');
  });

  it('should not submit if form invalid', () => {
    component.ngOnInit();
    component.addActivity();

    // do not fill form to make it invalid
    component.submit();

    expect(component.loading).toBe(false);
    expect(jobServiceMock.createActivities).not.toHaveBeenCalled();
  });
});
