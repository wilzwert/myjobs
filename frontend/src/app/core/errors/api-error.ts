import { HttpErrorResponse } from "@angular/common/http";
import { BackendError } from "./backend-error";

export class ApiError extends Error {
    httpStatus: number;
    originalError: Error;
    errors: Record<string, BackendError[]>;
    
    constructor(originalError: HttpErrorResponse) {
        const message: string = originalError.error?.message ?? 'Unable to load data';
        super(message);
        this.httpStatus = originalError.status;
        this.originalError = originalError;
        this.errors = (originalError.error?.errors?? {});
    }
}