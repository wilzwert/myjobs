import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { JobService } from '@core/services/job.service';
import { Job } from '@core/model/job.interface';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { EditorComponent, TINYMCE_SCRIPT_SRC } from '@tinymce/tinymce-angular'
import { NotificationService } from '@core/services/notification.service';
import { UpdateJobRequest } from '@core/model/update-job-request.interface';
import { CreateJobRequest } from '@core/model/create-job-request.interface';
import { catchError, Observable, take } from 'rxjs';
import { ApiError } from '@core/errors/api-error';
import { MatButton } from '@angular/material/button';
import { JobMetadata } from '@core/model/job-metadata.interface';
import { ErrorProcessorService } from '@core/services/error-processor.service';
import { CommentInputComponent } from '../inputs/comment-input.component';
import { SalaryInputComponent } from '../inputs/salary-input.component';
import { ProfileInputComponent } from '../inputs/profile-input.component';
import { DescriptionInputComponent } from '../inputs/description-input.component';
import { CompanyInputComponent } from '../inputs/company-input.component';
import { TitleInputComponent } from '../inputs/title-input.component';
import { UrlInputComponent } from '../inputs/url-input.component';

@Component({
  selector: 'app-job-form',
  imports: [ReactiveFormsModule, MatButton, CommentInputComponent, SalaryInputComponent, ProfileInputComponent, DescriptionInputComponent, CompanyInputComponent, TitleInputComponent, UrlInputComponent],
  providers: [
    { provide: TINYMCE_SCRIPT_SRC, useValue: 'tinymce/tinymce.min.js' }
  ],
  templateUrl: './job-form.component.html',
  styleUrl: './job-form.component.scss'
})
export class JobFormComponent implements OnInit {
  @Input() job: Job | null = null;
  @Input() jobMetadata: JobMetadata | null = null;
  @Output() jobSaved = new EventEmitter<Job>();

  protected valueSource: Job | JobMetadata | null = null;
  public loading = false;
  public error: string |null = null;
  public form: FormGroup | undefined;

  init: EditorComponent['init'] = {
    plugins: ['link', 'autolink', 'lists'],
    toolbar: 'undo redo | bold italic | link | bullist',
    promotion: false,
    menubar: 'null',
    statusbar: false
  };

  constructor(
    private readonly jobService: JobService, 
    private readonly notificationService: NotificationService, 
    private readonly fb: FormBuilder, 
    private readonly errorProcessorService: ErrorProcessorService
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  get url() {
    return this.form?.get('url');
  }

  get title() {
    return this.form?.get('title');
  }

  get company() {
    return this.form?.get('company');
  }

  get description() {
    return this.form?.get('description');
  }

  get profile() {
    return this.form?.get('profile');
  }

  get comment() {
    return this.form?.get('comment');
  }

  get salary() {
    return this.form?.get('salary');
  }

  updateRichText(event: any, field: string) :boolean {
    const control: AbstractControl<any>|null|undefined = this.form?.get(field);
    
    if(control !== null) {
      control?.setValue(event.editor.getContent());
    }
    return true;
  }

  submit() {
    this.error = null;
    this.loading = true;
    let observableResult: Observable<Job>;
    let term = $localize `:@@info.job.created:created`;
    if(this.job !== null && this.job.id !== null) {
      observableResult = this.jobService.updateJob(this.job.id, this.form?.value as UpdateJobRequest);
      term = $localize `:@@info.job.updated:updated`;
    }
    else {
      observableResult = this.jobService.createJob(this.form?.value as CreateJobRequest);
    }

    observableResult.pipe(
      take(1),
      catchError(
        (error: ApiError) => {
          this.loading = false;
          // set an explicit error message
          const errorMessage = $localize `:@@error.job.create_or_save:Job could not be ${term}.${error.message}`;
          error.message = errorMessage;
          this.error = errorMessage;
          return this.errorProcessorService.processError(error);
        }
      )
    )
    .subscribe((job) => {
      this.loading = false;
      this.notificationService.confirmation($localize `:@@job.created_or_saved:Job ${term} successfully`);
      this.jobSaved.emit(job);
    });
  }

  private initForm(): void {
    this.valueSource = this.job ? this.job : this.jobMetadata;
    this.form = this.fb.group({});
  }  
}