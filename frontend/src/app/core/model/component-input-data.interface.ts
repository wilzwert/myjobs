import { Job } from "./job.interface";
import { User } from "./user.interface";

export interface ComponentInputData {
    // TODO : avoid any if possible
    component: any,
    // TODO : avoid any if possible
    succeeded: any,
    // TODO : avoid any if possible
    failed?: any,

    data?: ComponentInputDomainData
}

export interface ComponentInputDomainData {
    user?: User,

    job?: Job,

    // TODO : avoid any if possible
    metadata? : any
}