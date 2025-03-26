import { TestBed } from '@angular/core/testing';

import { JobModalService } from './job-modal.service';

describe('JobModalService', () => {
  let service: JobModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(JobModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
