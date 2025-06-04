import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { Job } from '@app/core/model/job.interface';
import { JobEditableFieldComponent } from '../job-editable-field/job-editable-field.component';

@Component({
  selector: 'app-job-comment',
  imports: [MatTooltip, MatIcon, JobEditableFieldComponent],
  templateUrl: './job-comment.component.html',
  styleUrl: './job-comment.component.scss'
})
export class JobCommentComponent {
  @Input({ required: true }) job!: Job;
  @Output() commentChanged = new EventEmitter<Job>();

  constructor() {}

  onJobEdited(job: Job) {
    this.commentChanged.emit(job);
  }

}
