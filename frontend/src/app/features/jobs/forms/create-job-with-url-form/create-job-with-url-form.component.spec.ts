import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateJobWithUrlFormComponent } from './create-job-with-url-form.component';
import { of } from 'rxjs';
import { ModalService } from '@core/services/modal.service';
import { JobService } from '@core/services/job.service';
import { JobMetadata } from '@core/model/job-metadata.interface';
import { ComponentInputDomainData } from '@core/model/component-input-data.interface';

describe('CreateJobWithUrlFormComponent', () => {
  let component: CreateJobWithUrlFormComponent;
  
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
  
    const fbMock = new (require('@angular/forms').FormBuilder)();
  
    beforeEach(() => {
      jest.clearAllMocks();
  
      component = new CreateJobWithUrlFormComponent(
        fbMock,
        jobServiceMock,
        modalServiceMock
      );
  
      component.ngOnInit();
    });

    it('should create form with url control', () => {
      expect(component.urlForm).toBeDefined();
      expect(component.urlForm?.contains('url')).toBe(true);
      expect(component.url).not.toBeNull();
    });

    it('should fetch metadata and update data when getMetadataFromUrl is called', () => {
      // set @Input
      component.data = {} as ComponentInputDomainData;
      const mockMetadata: JobMetadata = {
        title: 'Mock Job',
        company: 'Mock Company',
        description: 'Job description',
        url: 'https://example.com/job',
        profile: '',
        salary: ''
      };

      jobServiceMock.getJobMetadata.mockReturnValue(of(mockMetadata));
      component.urlForm?.controls['url'].setValue('https://example.com/job');
  
      const successSpy = jest.spyOn(component as any, 'success');
  
      component.getMetadataFromUrl();
  
      expect(jobServiceMock.getJobMetadata).toHaveBeenCalledWith('https://example.com/job');
      expect(component.data.metadata).toEqual({ jobMetadata: mockMetadata });
      expect(successSpy).toHaveBeenCalled();
    });

    
});
