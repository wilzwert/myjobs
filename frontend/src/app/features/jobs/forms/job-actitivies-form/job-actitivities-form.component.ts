import { Component, EventEmitter, input, Input, model, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { JobService } from '@core/services/job.service';
import { Job } from '@core/model/job.interface';
import { catchError, take, throwError } from 'rxjs';
import { ApiError } from '@core/errors/api-error';
import { CreateJobActivitiesRequest } from '@core/model/create-job-activities-request.interface';
import { NotificationService } from '@core/services/notification.service';
import { CreateJobActivityRequest } from '@core/model/create-job-activity-request.interface';
import { MatButton } from '@angular/material/button';
import { UserActitivityType } from '@core/model/activity-type';
import { ActivityLabelPipe } from '@core/pipe/activity-label.pipe';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatIcon } from '@angular/material/icon';
import { ErrorProcessorService } from '@core/services/error-processor.service';
import { TranslatorService } from '@app/core/services/translator.service';


@Component({
  selector: 'app-job-activities-form',
  imports: [ActivityLabelPipe, ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatButton, MatSelect, MatOption, MatIcon],
  templateUrl: './job-actitivities-form.component.html',
  styleUrl: './job-actitivities-form.component.scss'
})
export class JobActivitiesFormComponent implements OnInit {
  @Input({ required: true }) job!: Job;
  @Input() defaultActivities = 0;
  @Output() activitiesSaved = new EventEmitter<Job>();

  loading = false;
  activityForm!: FormGroup;
  activityTypeKeys: string[] = [];

  constructor(
    private fb: FormBuilder, 
    private jobService: JobService, 
    private notificationService: NotificationService,
    private errorProcessorService: ErrorProcessorService,
    private translatorService: TranslatorService) {
    this.activityTypeKeys = Object.keys(UserActitivityType);
  }

  ngOnInit(): void {
    this.activityForm = this.fb.group({
      activities: this.fb.array([])
    });
    
    for(let a=0; a<this.defaultActivities; a++) {
      this.addActivity();
    }
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
                  // set an explicit error message
                  error.message = $localize `:@@error.activities.creation:Activities could not be created.`+this.translatorService.translateError(error.message);
                  return this.errorProcessorService.processError(error);
              }
            )
          )
          .subscribe((job) => {
            this.loading = false;
            this.notificationService.confirmation(
              activities.length > 1 ?
                $localize`:@@message.activities.created:${activities.length} activities created successfully`
                : $localize`:@@message.activity.created:Activity created successfully`
            );
            this.activitiesSaved.emit(job);
          });
    }
  }
}
