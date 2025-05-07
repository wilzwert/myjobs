import { TestBed } from '@angular/core/testing';

import { TranslatorService } from './translator.service';

describe('TranslatorService', () => {
  let service: TranslatorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TranslatorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return unknown error with code', () => {
    expect(service.translateError("some_code")).toEqual("An unknown error occurred some_code");
  });

  it('when code is INVALID_URL then should return message', () => {
    expect(service.translateError("INVALID_URL")).toEqual("Invalid url");
  });

  it('when job status is unknown then should return unknown', () => {
    expect(service.translateJobStatus("some_status")).toEqual("unknown");
  });

  it('when job status is known then should return its translation', () => {
    expect(service.translateJobStatus("PENDING")).toEqual("Pending");
  });

  it('when activity type is unknown then should return unknown', () => {
    expect(service.translateActivityType("some_type")).toEqual("unknown");
  })

  it('when activity type is known then should return its translation', () => {
    expect(service.translateActivityType("COMPANY_REFUSAL")).toEqual("Refusal (by company)");
  })
});
