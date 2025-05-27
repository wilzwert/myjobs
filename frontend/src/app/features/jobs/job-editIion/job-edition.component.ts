import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { JobFormComponent } from '@features/jobs/job-form/job-form.component';
import { JobActivitiesFormComponent } from '@features/jobs/job-actitivies-form/job-actitivities-form.component';
import { JobAttachmentsFormComponent } from '@features/jobs/job-attachments-form/job-attachments-form.component';
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
