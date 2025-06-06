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

  it('should return unknown error with code when details null', () => {
    expect(service.translateError("some_code", null)).toEqual("An unknown error occurred some_code");
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

  it('when job status meta is unknown then should return unknown', () => {
    expect(service.translateJobStatusMeta("some_status_meta")).toEqual("unknown");
  });

  it('when job status meta is known then should return its translation', () => {
    expect(service.translateJobStatusMeta("ACTIVE")).toEqual("Active");
  });

  it('when activity type is unknown then should return unknown', () => {
    expect(service.translateActivityType("some_type")).toEqual("unknown");
  })

  it('when activity type is known then should return its translation', () => {
    expect(service.translateActivityType("COMPANY_REFUSAL")).toEqual("Refusal (by company)");
  })

  it('when a parameterized error message is translated with params then substitution should be done', () => {
    // we know that the FIELD_VALUE_TOO_SMALL is parameterized with ${'min'}
    expect(service.translateError("FIELD_VALUE_TOO_SMALL", {min: "12"})).toEqual("Value must be at least 12");
  })
});
