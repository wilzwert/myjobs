import { Activity } from "./activity.interface"
import { Attachment } from "./attachment.interface"

export interface JobRating {
    value: number;
}

export interface Job {
    id: string,
    url: string,
    status: JobStatus,
    title: string,
    company: string,
    description: string,
    profile: string,
    salary: string,
    rating: JobRating,
    createdAt: string
    updatedAt: string,
    activities: Activity[],
    attachments: Attachment[]
}

export enum JobStatus {
    CREATED = "Created",
    PENDING = "Pending",
    RELAUNCHED = "Relaunched",
    APPLICANT_REFUSED = "Refused (by me)",
    COMPANY_REFUSED = "Refused (by company)"
}