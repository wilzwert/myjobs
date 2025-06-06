import { effect, Injectable, Signal, signal, WritableSignal } from '@angular/core';
import { DataStorageService } from './data-storage.service';
import { JobsListOptions } from '../model/jobs-list-options';
import { UserSummary } from '../model/user-summary.interface';
import { JobStatus, JobStatusMeta } from '../model/job.interface';
import { UserService } from './user.service';

// storage key for list options
const JOBS_FILTER_KEY = 'jobs-filter';

@Injectable({
  providedIn: 'root'
})
export class JobsListOptionsService {

  // first load is specific, we have to know if it has been done
  private firstLoadDone = false;
  // current list options 
  private jobsListOptions!: JobsListOptions;
  // every change of options result in a signal update
  private readonly jobsListOptionsSignal: WritableSignal<JobsListOptions | null> = signal(null); 

  constructor(private readonly userService: UserService, private readonly dataStorageService: DataStorageService) { 
    // a UserSummary change triggers a JobsListOptions change
    effect(() => {
      const userSummary = this.userService.getUserSummary()();

      // null summary only implies that the loading hasn't been done yet
      // we do nothing for now because we make sure to get a value in the UserService :
      // in case of a loading error it will be false
      // if loading is ok, then userSummary will be a UserSummary instance
      if(null === userSummary) {
        // wait for the summary to load
        return;
      }

      // forceReload will be used to force a list reload by setting forceReload(true) on the options
      let forceReload = false;
      if(!this.firstLoadDone) {
        // on first load, get options from storage, default to empty options
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

      let result = null;
      // summary may be false in case of a loading error
      if(userSummary !== false) {
        // checking options against user's summary
        result = this.checkOptions(userSummary);
      }
      // fallback to current options if needed
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
      if(!summary.jobStatuses[status] || summary.jobStatuses[status] === null) {
        removeStatus = true;
      }
    }
    
    if(currentStatusMeta !== null) {
      const statusMeta = JobStatusMeta[currentStatusMeta as keyof typeof JobStatusMeta];
      if(!summary.usableJobStatusMetas.includes(statusMeta)) {
        removeStatusMeta = true;
      }
    }

    if(removeStatus || removeStatusMeta) {
      // new options should reflect previous one, only status and/or statusMeta will be removed
      const newOptions = Object.assign(new JobsListOptions(), this.jobsListOptions);
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