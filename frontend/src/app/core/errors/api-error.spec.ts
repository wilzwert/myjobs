import { ApiError } from './api-error';
import { HttpErrorResponse, HttpHeaders } from '@angular/common/http';

describe('ApiError', () => {
  it('should initialize correctly with a valid HttpErrorResponse', () => {
    const mockErrorResponse: HttpErrorResponse = {
      status: 404,
      error: {
        message: 'Not Found',
        errors: {
          field1: ['This field is required'],
          field2: ['Invalid value'],
        },
      },
      headers: {} as HttpHeaders,
      statusText: 'Not Found',
      url: '',
    } as HttpErrorResponse;

    const apiError = new ApiError(mockErrorResponse);

    expect(apiError.message).toBe('Not Found');
    expect(apiError.httpStatus).toBe(404);
    expect(apiError.errors).toEqual({
      field1: ['This field is required'],
      field2: ['Invalid value'],
    });
  });

  it('should use the default message when no message is provided in error response', () => {
    const mockErrorResponse: HttpErrorResponse = {
      status: 500,
      error: {},
      headers: {} as HttpHeaders,
      statusText: 'Internal Server Error',
      url: '',
    } as HttpErrorResponse;

    const apiError = new ApiError(mockErrorResponse);

    expect(apiError.message).toBe('Unable to load data');
    expect(apiError.httpStatus).toBe(500);
    expect(apiError.errors).toEqual({});
  });

  it('should handle undefined errors field correctly', () => {
    const mockErrorResponse: HttpErrorResponse = {
      status: 400,
      error: {
        message: 'Bad Request',
      },
      headers: {} as HttpHeaders,
      statusText: 'Bad Request',
      url: '',
    } as HttpErrorResponse;

    const apiError = new ApiError(mockErrorResponse);

    expect(apiError.message).toBe('Bad Request');
    expect(apiError.httpStatus).toBe(400);
    expect(apiError.errors).toEqual({});
  });

  it('should handle empty error response correctly', () => {
    const mockErrorResponse: HttpErrorResponse = {
      status: 500,
      error: null,
      headers: {} as HttpHeaders,
      statusText: 'Internal Server Error',
      url: '',
    } as HttpErrorResponse;

    const apiError = new ApiError(mockErrorResponse);

    expect(apiError.message).toBe('Unable to load data');
    expect(apiError.httpStatus).toBe(500);
    expect(apiError.errors).toEqual({});
  });

  it('should correctly handle error with empty message but non-empty errors object', () => {
    const mockErrorResponse: HttpErrorResponse = {
      status: 422,
      error: {
        errors: {
          field1: ['Missing value'],
        },
      },
      headers: {} as HttpHeaders,
      statusText: 'Unprocessable Entity',
      url: '',
    } as HttpErrorResponse; 

    const apiError = new ApiError(mockErrorResponse);

    expect(apiError.message).toBe('Unable to load data');
    expect(apiError.httpStatus).toBe(422);
    expect(apiError.errors).toEqual({
      field1: ['Missing value'],
    });
  });
});
