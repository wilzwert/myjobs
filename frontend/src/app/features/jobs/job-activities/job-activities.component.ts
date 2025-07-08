import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Job } from '@core/model/job.interface';
import { JobActivitiesFormComponent } from '@app/features/jobs/forms/job-activities-form/job-activities-form.component';
import { ModalService } from '@core/services/modal.service';
import { DatePipe } from '@angular/common';
import { ActivityLabelPipe } from '@core/pipe/activity-label.pipe';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-job-activities',
  imports: [JobActivitiesFormComponent, DatePipe, ActivityLabelPipe, MatButton],
  templateUrl: './job-activities.component.html',
  styleUrl: './job-activities.component.scss'
})
export class JobActivitiesComponent implements OnInit {
  @Input({ required: true }) job!: Job;
  @Input() formMode = 'inline';
  @Output() activitiesSaved = new EventEmitter<Job>();

  protected displayForm = this.formMode === 'inline';

  constructor(private modalService: ModalService){}

  ngOnInit(): void {
    this.displayForm = this.formMode === 'inline';
  }

  addActivity(job: Job) :void {
    this.modalService.openJobModal('activities-form', job, () => this.onActivitiesSaved(job), {defaultActivities: 1});
  }

  onActivitiesSaved(job: Job): void {
    this.activitiesSaved.emit(job);
  }
}