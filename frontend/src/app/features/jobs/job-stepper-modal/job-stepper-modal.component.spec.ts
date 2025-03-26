import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobStepperModalComponent } from './job-stepper-modal.component';

describe('JobStepperModalComponent', () => {
  let component: JobStepperModalComponent;
  let fixture: ComponentFixture<JobStepperModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobStepperModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobStepperModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
