import { HttpErrorResponse } from "@angular/common/http";

export class ApiError extends Error {
    httpStatus: number;
    originalError: Error;

    constructor(originalError: HttpErrorResponse) {
        console.trace(originalError.status+'->'+originalError.message);
        const message: string = originalError.error?.message ?? 'Unable to load data';
        super(message);
        this.httpStatus = originalError.status;
        this.originalError = originalError;
    }
}