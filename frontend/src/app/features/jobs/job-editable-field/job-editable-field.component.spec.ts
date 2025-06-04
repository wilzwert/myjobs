import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobEditableFieldComponent } from './job-editable-field.component';

describe('JobEditableFieldComponent', () => {
  let component: JobEditableFieldComponent;
  let fixture: ComponentFixture<JobEditableFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobEditableFieldComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobEditableFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
