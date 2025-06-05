import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobAttachmentsFormComponent } from './job-attachments-form.component';

describe('JobAttachmentsFormComponent', () => {
  let component: JobAttachmentsFormComponent;
  let fixture: ComponentFixture<JobAttachmentsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobAttachmentsFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobAttachmentsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
