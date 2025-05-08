import { HttpErrorResponse } from "@angular/common/http";

export class ApiError extends Error {
    httpStatus: number;
    originalError: Error;
    errors: Record<string, string[]>;
    
    constructor(originalError: HttpErrorResponse) {
        const message: string = originalError.error?.message ?? 'Unable to load data';
        super(message);
        this.httpStatus = originalError.status;
        this.originalError = originalError;
        this.errors = (originalError.error?.errors?? {});
    }
}