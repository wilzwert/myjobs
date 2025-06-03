import { JobStatus, JobStatusMeta } from "./job.interface";

export interface UserSummary {
  jobsCount: number;
  activeJobsCount: number;
  inactiveJobsCount: number;
  lateJobsCount: number;
  jobStatuses: Map<JobStatus, number> ;
  usableJobStatusMetas: JobStatusMeta[];
}