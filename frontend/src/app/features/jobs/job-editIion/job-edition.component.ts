import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { JobFormComponent } from '../job-form/job-form.component';
import { JobActivitiesFormComponent } from '../job-actitivies-form/job-actitivities-form.component';
import { JobAttachmentsFormComponent } from '../job-attachments-form/job-attachments-form.component';
import { BaseChildComponent } from '../../../core/component/base-child.component';
import { JobAttachmentsComponent } from '../job-attachments/job-attachments.component';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'app-job-edition',
  imports: [JobFormComponent, JobActivitiesFormComponent, JobAttachmentsFormComponent, JobAttachmentsComponent],
  templateUrl: './job-edition.component.html',
  styleUrl: './job-edition.component.scss'
})
export class JobEditionComponent extends BaseChildComponent {
  constructor() {super()}
}
