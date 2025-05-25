import { JobStatusComponent } from './job-status.component';
import { JobService } from '../../../core/services/job.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Job, JobStatus } from '../../../core/model/job.interface';
import { of } from 'rxjs';

describe('JobStatusComponent', () => {
  let component: JobStatusComponent;
  let jobService: jest.Mocked<JobService>;
  let notificationService: jest.Mocked<NotificationService>;

  const mockJob: Job = {
    id: '1',
    title: 'Dev',
    company: 'OpenAI',
    url: '',
    status: JobStatus.CREATED,
    createdAt: new Date().toISOString(),
  } as unknown as Job;

  beforeEach(() => {
    jobService = {
      updateJobStatus: jest.fn(),
    } as any;

    notificationService = {
      confirmation: jest.fn(),
    } as any;

    component = new JobStatusComponent(jobService, notificationService);
    component.job = { ...mockJob };
  });

  describe('icon getter', () => {
    it.each([
      [JobStatus.CREATED, 'library_add_check'],
      [JobStatus.PENDING, 'hourglass_top'],
      [JobStatus.RELAUNCHED, 'refresh'],
      [JobStatus.APPLICANT_REFUSED, 'block'],
      [JobStatus.COMPANY_REFUSED, 'cancel'],
      [JobStatus.ACCEPTED, 'check_circle'],
    ])('should return %s for status %s', (status, expectedIcon) => {
      component.job.status = status;
      expect(component.icon).toBe(expectedIcon);
    });

    it('should return "help" for unknown status', () => {
      // @ts-expect-error simulate bad status
      component.job.status = 'UNKNOWN';
      expect(component.icon).toBe('help');
    });
  });

  describe('editJobStatus()', () => {
    it('should call jobService.updateJobStatus with correct params and notify on success', () => {
      const newStatus = JobStatus.PENDING;
      jobService.updateJobStatus.mockReturnValue(of({ ...mockJob, status: newStatus }));

      component.editJobStatus(mockJob, newStatus);

      expect(jobService.updateJobStatus).toHaveBeenCalledWith(mockJob.id, { status: newStatus });
      expect(notificationService.confirmation).toHaveBeenCalledWith(
        $localize`:@@info.job.status.updated:Status updated successfully.`
      );
    });
  });

  it('should initialize statusKeys with enum keys', () => {
    const keys = Object.keys(JobStatus);
    expect(component.statusKeys).toEqual(keys);
  });
});
