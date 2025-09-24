import { JobStatus, JobStatusMeta } from "./job.interface";

export class JobsListOptions {
    private page: number = 0;
    private itemsPerPage: number = 10;
    private jobStatus: keyof typeof JobStatus | null = null;
    private jobStatusMeta: keyof typeof JobStatusMeta | null = null;
    private searchQuery: string | null = null;
    private currentSort: string;
    private mustReload: boolean | null = null;


    constructor(jobsStatus: string | null = null, jobStatusMeta: string | null = null, searchQuery: string | null = null, sort: string | null = null) {
        this.jobStatus = jobsStatus as keyof typeof JobStatus;
        this.jobStatusMeta = jobStatusMeta as keyof typeof JobStatusMeta;
        this.searchQuery = searchQuery;
        this.currentSort = sort ?? 'createdAt,desc';
    }

    getCurrentPage(): number {
        return this.page;
    }

    getItemsPerPage(): number {
        return this.itemsPerPage;
    }

    getStatus() :string | null{
        return this.jobStatus;
    }

    getStatusMeta() :string | null{
        return this.jobStatusMeta;
    }

    getQuery(): string | null {
        return this.searchQuery;
    }
    
    getSort(): string {
        return this.currentSort;
    }

    getMustReload(): boolean | null {
        return this.mustReload;
    }

    equals(jobsListOptions: JobsListOptions) :boolean {
        return this.page === jobsListOptions.getCurrentPage()
            && this.itemsPerPage === jobsListOptions.getItemsPerPage()
            && this.jobStatus === jobsListOptions.getStatus() 
            && this.jobStatusMeta === jobsListOptions.getStatusMeta()
            && this.searchQuery === jobsListOptions.getQuery()
            && this.currentSort == jobsListOptions.getSort();
    }

    changePagination(page: number, itemsPerPage: number |null): this {
        this.page = page;
        if(itemsPerPage !== null) {
            this.itemsPerPage = itemsPerPage;
        }
        return  this;
    }
    
    forceReload(force: boolean | null): this {
        this.mustReload = force;
        return this;
    }

    query(searchQuery: string | null): this {
        this.searchQuery = searchQuery;
        return this;
    }

    sort(sort: string): this {
        this.currentSort = sort;
        return this;
    }

    /**
     * 
     * @param status the new job status as a string (keyof JobStatus)
     * @param statusMeta the new job "meta" status as a string (keyof JobStatusMeta)
     * @returns true if changed, false otherwise
     */
    filter(status: string | null, statusMeta: string | null): this {
        // having two properties to handle status or statusMeta filters may seem unclean, as they are exclusive at the moment
        // but it actually allow us to change our mind in the future and use both filters cumulatively 
        if(status !== null) {
            const newStatus: keyof typeof JobStatus = status as keyof typeof JobStatus;
            // clicking on the current status removes the filter
            if(newStatus === this.jobStatus) {
                this.jobStatus = null;
            }
            else {
                this.jobStatus = status as keyof typeof JobStatus;
            }
            this.jobStatusMeta = null;
        }
        else {
            const newStatusFilter: keyof typeof JobStatusMeta = statusMeta as keyof typeof JobStatusMeta;
            // clicking on the current status filter removes the filter
            if(newStatusFilter === this.jobStatusMeta) {
                this.jobStatusMeta = null;
            }
            else {
                this.jobStatusMeta = statusMeta as keyof typeof JobStatusMeta;
            }
            this.jobStatus = null;
        }

        return this;
    }
}