import { DataStorageService } from './data-storage.service';

describe('DataStorageService', () => {
  let service: DataStorageService;

  beforeEach(() => {
    service = new DataStorageService();
    localStorage.clear();
    jest.clearAllMocks();
  });

  it('should store and retrieve raw string', () => {
    service.setItem('key1', 'hello');
    expect(service.getRawItem('key1')).toBe('hello');
  });

  it('should store and retrieve an object', () => {
    const value = { name: 'Alice', age: 30 };
    service.setItem('key2', value);
    const result = service.getItem<typeof value>('key2');
    expect(result).toEqual(value);
  });

  it('should return null if key does not exist', () => {
    expect(service.getItem('unknown')).toBeNull();
    expect(service.getRawItem('unknown')).toBeNull();
  });

  it('should return null and log error if JSON is invalid', () => {
    const consoleSpy = jest.spyOn(console, 'log').mockImplementation();
    localStorage.setItem('bad', '{invalid json');
    expect(service.getItem('bad')).toBeNull();
    expect(consoleSpy).toHaveBeenCalled();
    consoleSpy.mockRestore();
  });

  it('should remove a key', () => {
    service.setItem('toDelete', 'bye');
    expect(service.getRawItem('toDelete')).toBe('bye');
    service.removeItem('toDelete');
    expect(service.getRawItem('toDelete')).toBeNull();
  });
});