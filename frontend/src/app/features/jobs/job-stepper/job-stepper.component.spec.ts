import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobStepperComponent } from './job-stepper.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentInputDomainData } from '@app/core/model/component-input-data.interface';
import { Job } from '@app/core/model/job.interface';
import { JobMetadata } from '@app/core/model/job-metadata.interface';

describe('JobStepperComponent', () => {
  let component: JobStepperComponent;
  let fixture: ComponentFixture<JobStepperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobStepperComponent],
      providers: [
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
      ] 
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobStepperComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    component.data = {
          job: {id: '1', title: 'Test Job', activities: [], url: '', attachments: [], company: '', description: '', comment: '', profile: '', salary: '', createdAt: '', rating: {value: 3}, status: 'PENDING', updatedAt: '', statusUpdatedAt: ''},
          metadata: {jobMetadata: {title: 'Extracted title'} as JobMetadata},
        } as ComponentInputDomainData;
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });
});
