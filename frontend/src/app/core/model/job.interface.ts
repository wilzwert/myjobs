import { Activity } from "./activity.interface"

export interface Job {
    id: string,
    title: string,
    url: string,
    description: string,
    profile: string,
    status: string
    createdAt: string
    updatedAt: string,
    activities: Activity[]
}