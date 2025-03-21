import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobActitivyFormComponent } from './job-actitivy-form.component';

describe('JobActitivyFormComponent', () => {
  let component: JobActitivyFormComponent;
  let fixture: ComponentFixture<JobActitivyFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobActitivyFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobActitivyFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
