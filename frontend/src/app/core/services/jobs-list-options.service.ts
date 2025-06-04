import { effect, Injectable, Signal, signal, WritableSignal } from '@angular/core';
import { DataStorageService } from './data-storage.service';
import { JobsListOptions } from '../model/jobs-list-options';
import { UserSummary } from '../model/user-summary.interface';
import { JobStatus, JobStatusMeta } from '../model/job.interface';
import { UserService } from './user.service';

const JOBS_FILTER_KEY = 'jobs-filter';

@Injectable({
  providedIn: 'root'
})
export class JobsListOptionsService {

  private firstLoadDone = false;
  private jobsListOptions!: JobsListOptions;
  private jobsListOptionsSignal: WritableSignal<JobsListOptions | null> = signal(null); 

  constructor(private userService: UserService, private dataStorageService: DataStorageService) { 

    // a UserSummary change triggers a JobsListOptions change
    effect(() => {
      const userSummary = this.userService.getUserSummary()();
      if(!userSummary) {
        return;
      }
      let forceReload = false;
      if(!this.firstLoadDone) {
        // on first load, get options from storage, default to empty options, check UserSummary consistency then save
        const storedOptions = this.dataStorageService.getItem<JobsListOptions>(JOBS_FILTER_KEY);
        let newOptions = null === storedOptions ? new JobsListOptions() : Object.assign(new JobsListOptions(), storedOptions);
        // no need to force reloading when loading options from storage, as it is done before any jobs list loading
        newOptions.forceReload(null);
        this.jobsListOptions = newOptions;
        this.firstLoadDone = true;
      }
      // when summary changes after first load it may indicate a change in the jobs list, whether it's filtered or not
      // therefore we have to reload the list
      else {
        forceReload = true;
      }

      // checking options against user's summary
      let result = this.checkOptions(userSummary);
      if(!(result instanceof JobsListOptions)) {
        result = Object.assign(new JobsListOptions(), this.jobsListOptions);
      }

      if(forceReload) {
        result.forceReload(true);
        result.changePagination(0, null);
      }

      this.save(result);
    })
  }

  public clear() :void {
    this.save(new JobsListOptions());
  }

  public getCurrentOptions(): JobsListOptions {
    return this.jobsListOptions;
  }

  public getJobsListOptions(): Signal<JobsListOptions | null> {
    return this.jobsListOptionsSignal.asReadonly();
  }

  private copyOptions() :JobsListOptions {
    return Object.assign(new JobsListOptions, this.jobsListOptions);
  }

  public changePagination(page: number, itemsPerPage: number | null): void {
    const newOptions = this.copyOptions();
    this.save(newOptions.changePagination(page, itemsPerPage));
  }

  public filter(status: string | null, statusMeta: string | null): void {
    const newOptions = this.copyOptions();
    this.save(newOptions.filter(status, statusMeta));
  }

  public sort(sort: string): void {
    const newOptions = this.copyOptions();
    this.save(newOptions.sort(sort));
  }

  // checks if current options are compatible with a UserSummary
  // and updates filters if necessary
  public checkOptions(summary: UserSummary): JobsListOptions | null {
    let removeStatus, removeStatusMeta = false;
    const currentStatus = this.jobsListOptions.getStatus();
    const currentStatusMeta = this.jobsListOptions.getStatusMeta();

    if(currentStatus !== null) {
      const status = JobStatus[currentStatus as keyof typeof JobStatus];
      if(!summary.jobStatuses[status] || summary.jobStatuses[status] === null || summary.jobStatuses[status]! < 1) {
        removeStatus = true;
      }
    }
    
    if(currentStatusMeta !== null) {
      const statusMeta = JobStatusMeta[currentStatusMeta as keyof typeof JobStatusMeta];
      if(summary.usableJobStatusMetas.includes(statusMeta)) {
        removeStatusMeta = true;
      }
    }

    if(removeStatus || removeStatusMeta) {
      const newOptions = new JobsListOptions();
      newOptions.filter(removeStatus ? null : currentStatus, removeStatusMeta ? null : currentStatusMeta);
      return newOptions;
    }

    return null;
  }

  private save(options: JobsListOptions) :void {
    this.jobsListOptions = options;
    // create a new instance so that the signal detects the change
    this.jobsListOptionsSignal.set(this.jobsListOptions);
    
    // store in the data storage for later
    this.dataStorageService.setItem(JOBS_FILTER_KEY, this.jobsListOptions);
  }
}