import { Activity } from "./activity.interface"
import { Attachment } from "./attachment.interface"

export interface Job {
    id: string,
    url: string,
    status: string
    title: string,
    company: string,
    description: string,
    profile: string,
    createdAt: string
    updatedAt: string,
    activities: Activity[],
    attachments: Attachment[]
}