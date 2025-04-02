import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { JobService } from '../../../core/services/job.service';
import { Job } from '../../../core/model/job.interface';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { EditorComponent, TINYMCE_SCRIPT_SRC } from '@tinymce/tinymce-angular'
import { NotificationService } from '../../../core/services/notification.service';
import { MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { UpdateJobRequest } from '../../../core/model/update-job-request.interface';
import { CreateJobRequest } from '../../../core/model/create-job-request.interface';
import { MatInput } from '@angular/material/input';
import { catchError, Observable, take, throwError } from 'rxjs';
import { ApiError } from '../../../core/errors/api-error';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-job-form',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatHint, MatIcon, MatButton, EditorComponent],
  providers: [
    { provide: TINYMCE_SCRIPT_SRC, useValue: 'tinymce/tinymce.min.js' }
  ],
  templateUrl: './job-form.component.html',
  styleUrl: './job-form.component.scss'
})
export class JobFormComponent implements OnInit {
  @Input() job: Job | null = null;
  @Output() jobSaved = new EventEmitter<Job>();

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

  constructor(private jobService: JobService, private notificationService: NotificationService, private fb: FormBuilder) {
  }
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
    let term = 'created';
    if(this.job !== null && this.job.id !== null) {
      observableResult = this.jobService.updateJob(this.job.id, this.form?.value as UpdateJobRequest);
      term = 'updated';
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
          error.message = `Job could not be ${term}.${error.message}`
          return throwError(() => error);
        }
      )
    )
    .subscribe((job) => {
      this.loading = false;
      this.notificationService.confirmation(`Job ${term} successfully`);
      this.jobSaved.emit(job);
    });
  }

  private initForm(): void {
    this.form = this.fb.group({
      url: [
        this.job?.url,
        [
          Validators.required,
          Validators.pattern('(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)')
        ]
      ],
      title: [
        this.job?.title, 
        [
          Validators.required,
          Validators.minLength(3)
        ]
      ],
      company: [
        this.job?.company, 
        [
          Validators.required,
          Validators.minLength(2)
        ]
      ],
      description: [
        this.job?.description,
        [
          Validators.required
        ]
      ],
      profile: [
        this.job?.profile,
        [
          
        ]
      ],
      salary: [
        this.job?.salary,
        [
          
        ]
      ]
    });
  }
  
}
