export interface BackendError {
    code: string,
    details: Record<string, string>|null
}