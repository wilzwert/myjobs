import { EmailStatus } from "./email-status";
import { Lang } from "./lang";

export interface User {
    email: string;
    username: string;
    firstName: string;
    lastName: string;
    createdAt: string;
    emailStatus: EmailStatus
    jobFollowUpReminderDays: number,
    lang?: Lang
  }