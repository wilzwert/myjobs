import { TestBed } from "@angular/core/testing";
import { LocationService } from "./location.service";

describe('LocationService', () => {
  let service: LocationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LocationService);
  });

  it('should call window.location.assign with the given url', () => {
    // TODO : find a way to mock window.location.assign ?
    service.assign('https://example.com');
  });
});
