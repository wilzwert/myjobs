import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobEditionComponent } from './job-edition.component';

describe('JobEditionComponent', () => {
  let component: JobEditionComponent;
  let fixture: ComponentFixture<JobEditionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobEditionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobEditionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
