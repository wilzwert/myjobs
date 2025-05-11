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
    statusUpdatedAt: string,
    activities: Activity[],
    attachments: Attachment[],
    // for now it remains optional because 
    // the backend doesn't ensure this info is always provided
    followUpLate?: boolean
}

export enum JobStatus {
    CREATED = "Created",
    PENDING = "Pending",
    RELAUNCHED = "Relaunched",
    APPLICANT_REFUSED = "Refused (by me)",
    COMPANY_REFUSED = "Refused (by company)"
}