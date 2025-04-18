import { of } from 'rxjs';
import { GlobalErrorHandler } from './global-error.handler';
import { NotificationService } from './notification.service';

describe('FileService', () => {
  let globalErrorHandler: GlobalErrorHandler;
  let noticationService: jest.Mocked<NotificationService>;
  
  beforeEach(() => {
    noticationService = {
      error: jest.fn()
    } as unknown as jest.Mocked<NotificationService>;

    globalErrorHandler = new GlobalErrorHandler(noticationService);
  });

  afterAll(() => {
    jest.resetAllMocks();
  })

  it('should be created', () => {
    expect(globalErrorHandler).toBeTruthy();
  });

  it('should open error notification with message', () => {
    const mockError = {message: 'Test error message'} as Error;

    globalErrorHandler.handleError(mockError);

    expect(noticationService.error).toHaveBeenCalledTimes(1);
    expect(noticationService.error).toHaveBeenCalledWith(
      'Test error message',
      mockError
    );
  });

  it('should open error notification with empty message', () => {
    const mockError = {} as Error;

    globalErrorHandler.handleError(mockError);

    expect(noticationService.error).toHaveBeenCalledTimes(1);
    expect(noticationService.error).toHaveBeenCalledWith(
      '',
      mockError
    );
  });

});
