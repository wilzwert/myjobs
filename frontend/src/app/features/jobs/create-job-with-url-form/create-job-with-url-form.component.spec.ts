import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateJobWithUrlFormComponent } from './create-job-with-url-form.component';

describe('CreateJobWithUrlFormComponent', () => {
  let component: CreateJobWithUrlFormComponent;
  let fixture: ComponentFixture<CreateJobWithUrlFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateJobWithUrlFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateJobWithUrlFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
