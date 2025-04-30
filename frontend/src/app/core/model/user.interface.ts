import { EmailStatus } from "./email-status";
import { Lang } from "./lang";

export interface User {
    email: string;
    username: string;
    firstName: string;
    lastName: string;
    createdAt: string;
    emailStatus: EmailStatus
    lang?: Lang
  }