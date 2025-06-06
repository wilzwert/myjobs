import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JobCommentComponent } from './job-comment.component';
import { Job } from '@app/core/model/job.interface';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('JobCommentComponent', () => {
  let component: JobCommentComponent;
  let fixture: ComponentFixture<JobCommentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobCommentComponent],
      providers: [
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(JobCommentComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit commentChanged on onJobEdited call', () => {
    const job: Job = { id: '123', title: 'Test Job', comment: 'Initial comment' } as Job;
    jest.spyOn(component.commentChanged, 'emit');

    component.onJobEdited(job);

    expect(component.commentChanged.emit).toHaveBeenCalledWith(job);
  });
});
