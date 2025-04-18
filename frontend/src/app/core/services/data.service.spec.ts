import { DataService } from './data.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { firstValueFrom, of } from 'rxjs';

describe('DataService', () => {
  let dataService: DataService;
  let httpClientMock: jest.Mocked<HttpClient>;
  
  beforeEach(() => {
    httpClientMock = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      patch: jest.fn(),
      delete: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    dataService = new DataService(httpClientMock);
  });

  afterAll(() => {
    jest.resetAllMocks();
  })

  it('should be created', () => {
    expect(dataService).toBeTruthy();
  });

  it('should trigger a GET request and return expected data', async () => {
    const mockData = { id: 1, title: ' Test topic', description: 'Test topic description' };

    httpClientMock.get.mockReturnValue(of(mockData));

    const data = await firstValueFrom(dataService.get<typeof mockData>('jobs'));
    expect(data).toEqual(mockData);
    expect(httpClientMock.get).toHaveBeenCalledWith('api/jobs', {
      withCredentials: true
    });
  });

  it('shoult trigger a POST request and return expected data', async () => {
    const mockResponse = { id: 1, title: 'Test article', content: 'Test content' };
    const payload = { content: 'Test content' };

    httpClientMock.post.mockReturnValue(of(mockResponse));

    const data = await firstValueFrom(dataService.post<typeof mockResponse>('jobs', payload));
    expect(data).toEqual(mockResponse);
    expect(httpClientMock.post).toHaveBeenCalledWith('api/jobs', payload, {
      withCredentials: true
    });

    
  });

  it('shoult trigger a PUT request and return expected data', async () => {
    const mockResponse = { id: 1, title: 'Test article', content: 'Test content' };
    const payload = { content: 'Test content' };

    httpClientMock.put.mockReturnValue(of(mockResponse));

    const data = await firstValueFrom(dataService.put<typeof mockResponse>('jobs/12', payload));
    expect(data).toEqual(mockResponse);
    expect(httpClientMock.put).toHaveBeenCalledWith('api/jobs/12', payload, {
      withCredentials: true
    });
  });

  it('shoult trigger a DELETE request and return expected data', async () => {
    httpClientMock.delete.mockReturnValue(of(null));

    const data = await firstValueFrom(dataService.delete<null>('jobs/12'));
    expect(data).toEqual(null);
    expect(httpClientMock.delete).toHaveBeenCalledWith('api/jobs/12', {
      withCredentials: true
    });

  });

  it('should pass headers and  params', () => {
    const params = new HttpParams().set('q', 'test');
    const headers = new HttpHeaders().set('Captcha-Response', 'captcha-token');
    httpClientMock.get.mockReturnValue(of('ok'));

    dataService.get<string>('jobs', { params, headers }).subscribe();

    expect(httpClientMock.get).toHaveBeenCalledWith('api/jobs', {
      withCredentials: true,
      params,
      headers
    });
  });

});
