import { EmailStatus } from "./email-status";

export interface User {
    email: string;
    username: string;
    firstName: string;
    lastName: string;
    createdAt: string;
    emailStatus: string
  }