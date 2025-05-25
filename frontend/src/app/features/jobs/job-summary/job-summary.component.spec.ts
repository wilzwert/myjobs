import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobSummaryComponent } from './job-summary.component';
import { JobStatusComponent } from '../job-status/job-status.component';
import { JobService } from '../../../core/services/job.service';
import { ModalService } from '../../../core/services/modal.service';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { NotificationService } from '../../../core/services/notification.service';
import { of } from 'rxjs';
import { Job, JobStatus } from '../../../core/model/job.interface';

describe('JobSummaryComponent', () => {
  let component: JobSummaryComponent;
  
    // Mocks des services injectés
    const jobServiceMock = {
      getCurrentPage: jest.fn(),
      getItemsPerPage: jest.fn(),
      getAllJobs: jest.fn(),
      updateJobStatus: jest.fn(),
      updateJobRating: jest.fn(),
      getJobMetadata: jest.fn(),
      deleteJob: jest.fn(),
    } as unknown as jest.Mocked<JobService>;
  
    const modalServiceMock = {
      openJobStepperModal: jest.fn(),
      openJobModal: jest.fn(),
    } as unknown as jest.Mocked<ModalService>;
  
    const confirmDialogServiceMock = {
      openConfirmDialog: jest.fn(),
    } as unknown as jest.Mocked<ConfirmDialogService>;
  
    const notificationServiceMock = {
      confirmation: jest.fn(),
    } as unknown as jest.Mocked<NotificationService>;;

    beforeEach(() => {
      jest.clearAllMocks();
      component = new JobSummaryComponent(jobServiceMock, notificationServiceMock, modalServiceMock, confirmDialogServiceMock);
    })

    it('should update job status and show notification', done => {
        jobServiceMock.updateJobStatus.mockReturnValue(of({ id: 'job1', status: JobStatus.RELAUNCHED } as Job));
    
        const job = { id: 'job1', status: JobStatus.PENDING } as Job;
    
        component.editJobStatus(job, JobStatus.RELAUNCHED);
    
        setTimeout(() => {
          expect(jobServiceMock.updateJobStatus).toHaveBeenCalledWith('job1', { status: JobStatus.RELAUNCHED } as any);
          expect(notificationServiceMock.confirmation).toHaveBeenCalledWith(expect.stringContaining('Status updated successfully'));
          done();
        }, 0);
      });
    
      it('should update job rating and show notification', done => {
        jobServiceMock.updateJobRating.mockReturnValue(of({ id: 'job1', rating: 4 } as unknown as Job));
    
        const job = { id: 'job1', rating: 3 } as unknown as Job;
    
        component.updateJobRating(job, 4);
    
        setTimeout(() => {
          expect(jobServiceMock.updateJobRating).toHaveBeenCalledWith('job1', { rating: 4 });
          expect(notificationServiceMock.confirmation).toHaveBeenCalledWith(expect.stringContaining('Rating updated successfully'));
          done();
        }, 0);
      });

      it('should edit job and open modal', () => {
        const job = { id: 'job1' } as Job;
        const event = { stopPropagation: jest.fn() } as any;

        component.editJob(event, job);

        expect(modalServiceMock.openJobModal).toHaveBeenCalledWith('job', job, expect.any(Function));
      });
  
});
