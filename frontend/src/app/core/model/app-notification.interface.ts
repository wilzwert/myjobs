import { ApiError } from "@core/errors/api-error";

export interface AppNotification {
    type: 'error' | 'confirmation' | 'information';
    error: Error | ApiError | null;
    message: string;
}