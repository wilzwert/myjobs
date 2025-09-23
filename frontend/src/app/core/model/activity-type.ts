export enum ActivityType {
    CREATION = "Job creation",
    ATTACHMENT_CREATION = "Attachment creation",
    ATTACHMENT_DELETION = "Attachment deletion",
    APPLICATION = "Application",
    RELAUNCH = "Application relaunch",
    EMAIL = "Mail sent",
    TEL_INTERVIEW = "Phone interview",
    VIDEO_INTERVIEW = "Video interview",
    IN_PERSON_INTERVIEW = "In-person interview",  
    APPLICANT_REFUSAL = "Refused by applicant",
    COMPANY_REFUSAL = "Refused by company",
    RATING = "Rating",
    JOB_EXPIRATION = "Job expired",
    JOB_CANCELLATION = "Job cancelled",
    ACCEPTANCE = "Job accepted",
    HIRING = "Hired"

}

// activities types that may be manually added by a user
export enum UserActitivityType {
    APPLICATION = "A",
    RELAUNCH = "R",
    EMAIL = "E",
    TEL_INTERVIEW = "TI",
    VIDEO_INTERVIEW = "VI",
    IN_PERSON_INTERVIEW = "PI",  
    APPLICANT_REFUSAL = "AR",
    COMPANY_REFUSAL = "CR",
    JOB_EXPIRATION = "JE",
    JOB_CANCELLATION = "JC",
    ACCEPTANCE = "AC",
    HIRING = "H"
}