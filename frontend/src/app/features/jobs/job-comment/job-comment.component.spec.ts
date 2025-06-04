import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobCommentComponent } from './job-comment.component';

describe('JobCommentComponent', () => {
  let component: JobCommentComponent;
  let fixture: ComponentFixture<JobCommentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobCommentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobCommentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
