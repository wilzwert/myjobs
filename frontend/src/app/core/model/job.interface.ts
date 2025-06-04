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
    comment: string,
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
    CREATED = "CREATED",
    PENDING = "PENDING",
    RELAUNCHED = "RELAUNCHED",
    APPLICANT_REFUSED = "APPLICANT_REFUSED",
    COMPANY_REFUSED = "COMPANY_REFUSED",
    EXPIRED = "EXPIRED",
    CANCELLED = "CANCELLED",
    ACCEPTED = "ACCEPTED",
    HIRED = "HIRED"
}

export enum JobStatusMeta { 
    ACTIVE = "ACTIVE",
    INACTIVE = "INACTIVE",
    LATE = "LATE"
}