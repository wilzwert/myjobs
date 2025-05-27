import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { StatusIconComponent } from '../../../layout/shared/status-icon/status-icon.component';
import { JobService } from '../../../core/services/job.service';
import { JobMetadata } from '../../../core/model/job-metadata.interface';
import { BaseChildComponent } from '../../../core/component/base-child.component';
import { ModalService } from '../../../core/services/modal.service';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-create-job-with-url-form',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatHint, MatLabel, StatusIconComponent, MatButton],
  templateUrl: './create-job-with-url-form.component.html',
  styleUrl: './create-job-with-url-form.component.scss'
})
export class CreateJobWithUrlFormComponent extends BaseChildComponent implements OnInit {

  @Output() onJobCreation = new EventEmitter<JobMetadata>;

  public urlForm: FormGroup | undefined;
  public urlFormLoading = false;

  constructor(private fb: FormBuilder, private jobService: JobService, private modalService: ModalService) {
    super();
  }

  ngOnInit(): void {
    this.urlForm = this.fb.group({
      url: [
        '',
        [
          Validators.required,
          Validators.pattern('(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)')
        ]
      ],
    });
  }

  get url() {
    return this.urlForm?.get('url');
  }


  getMetadataFromUrl(): void {
    this.jobService.getJobMetadata(this.url?.value).subscribe((metadata: JobMetadata) => {
      this.data.metadata = { jobMetadata: metadata };
      this.success();
    });
  }
}
