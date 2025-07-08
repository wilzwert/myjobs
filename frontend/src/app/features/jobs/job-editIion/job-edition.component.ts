import { Component } from '@angular/core';
import { JobFormComponent } from '@app/features/jobs/forms/job-form/job-form.component';
import { JobActivitiesFormComponent } from '@app/features/jobs/forms/job-activities-form/job-activities-form.component';
import { JobAttachmentsFormComponent } from '@app/features/jobs/forms/job-attachments-form/job-attachments-form.component';
import { BaseChildComponent } from '@core/component/base-child.component';
import { JobAttachmentsComponent } from '@features/jobs/job-attachments/job-attachments.component';

@Component({
  selector: 'app-job-edition',
  imports: [JobFormComponent, JobActivitiesFormComponent, JobAttachmentsFormComponent, JobAttachmentsComponent],
  templateUrl: './job-edition.component.html',
  styleUrl: './job-edition.component.scss'
})
export class JobEditionComponent extends BaseChildComponent {
  constructor() {super()}
}
