import { ActivityType } from "./activity-type";

export interface Activity {
    type: ActivityType,
    comment: string,
    createdAt: string
}