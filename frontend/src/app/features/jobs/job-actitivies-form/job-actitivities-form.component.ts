import { Component, EventEmitter, input, Input, model, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { JobService } from '../../../core/services/job.service';
import { Job } from '../../../core/model/job.interface';
import { catchError, take, throwError } from 'rxjs';
import { ApiError } from '../../../core/errors/api-error';
import { CreateJobActivitiesRequest } from '../../../core/model/create-job-activities-request.interface';
import { NotificationService } from '../../../core/services/notification.service';
import { CreateJobActivityRequest } from '../../../core/model/create-job-activity-request.interface';
import { MatButton } from '@angular/material/button';
import { ActivityType, UserActitivityType } from '../../../core/model/activity-type';
import { ActivityLabelPipe } from '../../../core/pipe/activity-label.pipe';
import { MatOption, MatSelect } from '@angular/material/select';


@Component({
  selector: 'app-job-activities-form',
  imports: [ActivityLabelPipe, ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatButton, MatSelect, MatOption],
  templateUrl: './job-actitivities-form.component.html',
  styleUrl: './job-actitivities-form.component.scss'
})
export class JobActivitiesFormComponent implements OnInit {
  @Input({ required: true }) job!: Job;
  @Output() activitiesSaved = new EventEmitter<Job>();

  loading = false;
  activityForm!: FormGroup;
  ActivityType = ActivityType;
  activityTypeKeys: string[] = [];

  constructor(private fb: FormBuilder, private jobService: JobService, private notificationService: NotificationService) {
    this.activityTypeKeys=Object.keys(UserActitivityType);
  }

  ngOnInit(): void {
    this.activityForm = this.fb.group({
      activities: this.fb.array([])
    });
  }

  get activities(): FormArray {
    return this.activityForm.get('activities') as FormArray;
  }

  addActivity(): void {
    const activityGroup = this.fb.group({
      type: ['', Validators.required],
      comment: ['', Validators.required],
    });
    this.activities.push(activityGroup);
  }

  removeActivity(index: number): void {
    this.activities.removeAt(index);
  }

  submit(): void {
    if (this.activityForm.valid) {
      this.loading = true;
      const activities: {}[] = new Array();
      
      this.activities.controls.forEach((activity, index) => {
        activities[index] = {'type': activity.value.type, 'comment':  activity.value.comment} as CreateJobActivityRequest;
      });

      this.jobService.createActivities(this.job.id, {activities: activities} as CreateJobActivitiesRequest).pipe(
            take(1),
            catchError(
              (error: ApiError) => {
                  this.loading = false;
                  return throwError(() => new Error(
                    `Activities could not be created.${error.message}`
                  ));
              }
            )
          )
          .subscribe((job) => {
            this.loading = false;
            this.notificationService.confirmation(`Activit${activities.length > 1 ? 'ies' : 'y'} created successfully`);
            this.activitiesSaved.emit(job);
          });
    }
  }
}
