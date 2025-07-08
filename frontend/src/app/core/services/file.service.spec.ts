import { HttpClient } from '@angular/common/http';
import { firstValueFrom, of } from 'rxjs';
import { FileService } from './file.service';

describe('FileService', () => {
  let fileService: FileService;
  let httpClientMock: jest.Mocked<HttpClient>;
  
  beforeEach(() => {
    httpClientMock = {
      get: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    fileService = new FileService(httpClientMock);
  });

  afterAll(() => {
    jest.resetAllMocks();
  })

  it('should be created', () => {
    expect(fileService).toBeTruthy();
  });

  it('should trigger a GET request with an expected blob responseType', async () => {
    const mockData = new Blob(["test-file"]);

    httpClientMock.get.mockReturnValue(of(mockData));
    
    const data = await firstValueFrom(fileService.downloadFile('http://www.example.com/test-file'));
    expect(data).toEqual(mockData);

    expect(httpClientMock.get).toHaveBeenCalledWith('http://www.example.com/test-file', {
      responseType: 'blob',
      withCredentials: false
    });
  });

});
