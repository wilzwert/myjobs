import { Activity } from "./activity.interface"

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
    activities: Activity[]
}