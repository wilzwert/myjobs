import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { RatingComponent } from './rating.component';
import { Job } from '@app/core/model/job.interface';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { JobService } from '@app/core/services/job.service';
import { NotificationService } from '@app/core/services/notification.service';

describe('RatingComponent', () => {
  let component: RatingComponent;
  let fixture: ComponentFixture<RatingComponent>;
  let jobServiceMock: any;
  let notificationServiceMock: any;

  beforeEach(async () => {
    jobServiceMock = {
      updateJobRating: jest.fn()
    };
    notificationServiceMock = {
      confirmation: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [RatingComponent],
      providers: [
        { provide: JobService, useValue: jobServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RatingComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize rating in ngOnInit', () => {
    component.job = { id: '1', rating: { value: 3 } } as Job;
    component.ngOnInit();
    expect(component.rating).toBe(3);
  });

  it('should return "star" or "star_border" in showIcon()', () => {
    component.rating = 3;
    expect(component.showIcon(0)).toBe('star');
    expect(component.showIcon(2)).toBe('star');
    expect(component.showIcon(3)).toBe('star_border');
  });

  it('should update job rating and emit event', () => {
    const job = { id: '1', rating: { value: 2 } } as Job;
    const updatedJob = { id: '1', rating: { value: 5 } } as Job;

    component.job = job;
    jobServiceMock.updateJobRating.mockReturnValue(of(updatedJob));
    jest.spyOn(component.ratingChange, 'emit');

    component.updateJobRating(job, 5);

    expect(jobServiceMock.updateJobRating).toHaveBeenCalledWith('1', { rating: 5 });
    expect(component.job).toBe(updatedJob);
    expect(component.rating).toBe(5);
    expect(component.ratingChange.emit).toHaveBeenCalledWith(updatedJob);
    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith('Rating updated successfully.');
  });
});