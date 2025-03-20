import { Component, OnInit } from '@angular/core';
import { JobService } from '../../core/services/job.service';
import { Observable } from 'rxjs';
import { Page } from '../../core/model/page.interface';
import { Job } from '../../core/model/job.interface';
import { AsyncPipe } from '@angular/common';
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator';



@Component({
  selector: 'app-jobs',
  imports: [AsyncPipe, MatPaginatorModule],
  templateUrl: './jobs.component.html',
  styleUrl: './jobs.component.scss'
})
export class JobsComponent implements OnInit {

  public jobs$!: Observable<Page<Job>>;

  constructor(private jobService: JobService) {}

  ngOnInit(): void {
    this.jobs$ = this.jobService.getAllPosts(0, 10);
  }

  handlePageEvent(event: PageEvent) {
    this.jobs$ = this.jobService.getAllPosts(event.pageIndex, event.pageSize);
  }
}
