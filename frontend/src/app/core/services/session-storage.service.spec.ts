// session-storage.service.spec.ts
import { SessionStorageService } from './session-storage.service';
import { DataStorageService } from './data-storage.service';
import { SessionInformation } from '@core/model/session-information.interface';
import { BehaviorSubject } from 'rxjs';

describe('SessionStorageService', () => {
  let service: SessionStorageService;
  let dataStorageMock: jest.Mocked<DataStorageService>;

  const fakeSessionInfo: SessionInformation = {
    email: 'user@example.com',
    username: 'user',
    role: 'USER'
  };

  beforeEach(() => {
    dataStorageMock = {
      getItem: jest.fn(),
      setItem: jest.fn(),
      removeItem: jest.fn()
    } as any;

    dataStorageMock.getItem.mockReturnValue(fakeSessionInfo);
    service = new SessionStorageService(dataStorageMock);
  });

  it('should load session info on construction', () => {
    expect(service.getSessionInformation()).toEqual(fakeSessionInfo);
    expect(dataStorageMock.getItem).toHaveBeenCalledWith('session-info');
  });

  it('should return session info from getter', () => {
    const info = service.getSessionInformation();
    expect(info).toEqual(fakeSessionInfo);
  });

  it('should expose session info via BehaviorSubject observable', () => {
    let value: SessionInformation | null = null;
    service.$getSessionInformation().subscribe(v => value = v);
    expect(value).toEqual(fakeSessionInfo);
  });

  it('should update session info and notify BehaviorSubject on save', () => {
    const newSession: SessionInformation = {
      email: 'user@example.com',
      username: 'user',
      role: 'USER'
    };

    let subjectValue: SessionInformation | null = null;
    service.$getSessionInformation().subscribe(v => subjectValue = v);

    service.saveSessionInformation(newSession);

    expect(dataStorageMock.setItem).toHaveBeenCalledWith('session-info', newSession);
    expect(service.getSessionInformation()).toBe(newSession);
    expect(subjectValue).toBe(newSession);
  });

  it('should clear session info and notify BehaviorSubject', () => {
    let subjectValue: SessionInformation | null = fakeSessionInfo;
    service.$getSessionInformation().subscribe(v => subjectValue = v);

    service.clearSessionInformation();

    expect(dataStorageMock.removeItem).toHaveBeenCalledWith('session-info');
    expect(service.getSessionInformation()).toBeNull();
    expect(subjectValue).toBeNull();
  });
});
