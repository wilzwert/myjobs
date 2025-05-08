import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JobActivitiesComponent } from './job-activities.component';
import { JobActivitiesFormComponent } from '../job-actitivies-form/job-actitivities-form.component';
import { JobService } from '../../../core/services/job.service';
import { ModalService } from '../../../core/services/modal.service';
import { DatePipe } from '@angular/common';
import { ActivityLabelPipe } from '../../../core/pipe/activity-label.pipe';
import { MatButtonModule } from '@angular/material/button';
import { By } from '@angular/platform-browser';
import { Job, JobRating, JobStatus } from '../../../core/model/job.interface';

describe('JobActivitiesComponent (Jest)', () => {
  let component: JobActivitiesComponent;
  let fixture: ComponentFixture<JobActivitiesComponent>;

  const mockModalService = {
    openJobModal: jest.fn()
  };

  const mockJobService = {
    someMethodIfUsed: jest.fn()
  };

  const fakeJob: Job = {
    id: '1',
    title: 'Test Job',
    activities: [],
    url: 'http://www.example.com',
    attachments: [],
    company: 'company',
    description: 'job description',
    profile: 'job profile',
    salary: 'job salary',
    createdAt: '',
    rating: { value: 3 } as JobRating,
    status: JobStatus.PENDING,
    updatedAt: ''
  } as Job;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        DatePipe,
        { provide: ModalService, useValue: mockModalService },
        { provide: JobService, useValue: mockJobService }
      ],
      imports: [MatButtonModule, JobActivitiesComponent, JobActivitiesFormComponent, ActivityLabelPipe],
    }).compileComponents();

    fixture = TestBed.createComponent(JobActivitiesComponent);
    component = fixture.componentInstance;
    component.job = fakeJob;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should set displayForm to true when formMode is inline', () => {
    component.formMode = 'inline';
    component.ngOnInit();
    expect(component['displayForm']).toBe(true);
  });

  it('should set displayForm to false when formMode is modal', () => {
    component.formMode = 'modal';
    component.ngOnInit();
    expect(component['displayForm']).toBe(false);
  });

  it('should render the form when displayForm is true', () => {
    component.formMode = 'inline';
    component.ngOnInit();
    fixture.detectChanges();

    const form = fixture.debugElement.query(By.directive(JobActivitiesFormComponent));
    expect(form).toBeTruthy();
  });

  it('should not render the form when displayForm is false', () => {
    component.formMode = 'modal';
    component.ngOnInit();
    fixture.detectChanges();

    const form = fixture.debugElement.query(By.directive(JobActivitiesFormComponent));
    expect(form).toBeNull();
  });

  it('should call modalService.openJobModal when addActivity is called and formMode is modal', () => {
    component.formMode = 'modal';
    fixture.detectChanges();

    component.addActivity(fakeJob);

    expect(mockModalService.openJobModal).toHaveBeenCalledWith(
      'activities-form',
      fakeJob,
      expect.any(Function),
      { defaultActivities: 1 }
    );
  });

  it('should emit activitiesSaved when onActivitiesSaved is called', () => {
    const emitSpy = jest.spyOn(component.activitiesSaved, 'emit');
    component.onActivitiesSaved(fakeJob);
    expect(emitSpy).toHaveBeenCalledWith(fakeJob);
  });

  it('should display activities form component when formMode is inline', () => {
    component.formMode = 'inline';
    fixture.detectChanges();

    const formComponent = fixture.debugElement.query(By.css('app-job-activities-form'));
    expect(formComponent).toBeTruthy();

    const button = fixture.debugElement.query(By.css('button'));
    expect(button).toBeDefined();
    expect(button.nativeElement.textContent).toEqual("Add an activity");
  });

  it('should open modal when add activity button clicke when formMode is not inline', () => {
    component.formMode = '';
    fixture.detectChanges();

    const button = fixture.debugElement.query(By.css('button'));
    expect(button).toBeDefined();
    expect(button.nativeElement.textContent).toEqual("Add activity");
    
    const addActivitySpy = jest.spyOn(component, 'addActivity');
    button.triggerEventHandler('click', fakeJob);
    expect(addActivitySpy).toHaveBeenCalledWith(fakeJob);
    expect(mockModalService.openJobModal).toHaveBeenCalled();
  });
});
