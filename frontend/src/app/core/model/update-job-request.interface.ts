export interface UpdateJobRequest {
    url: string,
    status: string
    title: string,
    company: string,
    description: string,
    profile: string,
    salary: string
}

export interface UpdateJobFieldRequest {
    url?: string,
    status?: string
    title?: string,
    company?: string,
    description?: string,
    profile?: string,
    salary?: string
}