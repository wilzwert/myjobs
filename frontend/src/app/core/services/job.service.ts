import { Injectable } from '@angular/core';
import { DataService } from './data.service';
import { Job } from '../model/job.interface';
import { Observable } from 'rxjs';
import { Page } from '../model/page.interface';

@Injectable({
  providedIn: 'root'
})
export class JobService {

  constructor(private dataService: DataService) { }

   /**
   * Retrieves the sorted jobs loded from the backend 
   * @returns the jobs
   */
  public getAllPosts(page: number, itemsPerPage: number): Observable<Page<Job>> {
    return this.dataService.get<Page<Job>>(`jobs?page=${page}&itemsPerPage=${itemsPerPage}`);
  }
}
