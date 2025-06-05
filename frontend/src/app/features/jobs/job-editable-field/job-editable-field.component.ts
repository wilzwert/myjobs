import { AfterContentInit, Component, ContentChild, ElementRef, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { Job } from '@app/core/model/job.interface';
import { CommentInputComponent } from '../forms/inputs/comment-input.component';
import { JobService } from '@app/core/services/job.service';
import { DescriptionInputComponent } from "../forms/inputs/description-input.component";
import { ProfileInputComponent } from '../forms/inputs/profile-input.component';
import { TitleInputComponent } from '../forms/inputs/title-input.component';
import { CompanyInputComponent } from "../forms/inputs/company-input.component";
import { UpdateJobFieldRequest } from '@app/core/model/update-job-request.interface';
import { NotificationService } from '@app/core/services/notification.service';
import { SalaryInputComponent } from "../forms/inputs/salary-input.component";
import { UrlInputComponent } from "../forms/inputs/url-input.component";

@Component({
  selector: 'app-job-editable-field',
  imports: [MatIcon, MatButton, MatTooltip, ReactiveFormsModule, CommentInputComponent, DescriptionInputComponent, ProfileInputComponent, TitleInputComponent, CompanyInputComponent, SalaryInputComponent, UrlInputComponent],
  templateUrl: './job-editable-field.component.html',
  styleUrl: './job-editable-field.component.scss'
})
export class JobEditableFieldComponent implements AfterContentInit {
  @Input({ required: true }) job!: Job;
  @Input({required: true}) field!: keyof Pick<Job, 'url' | 'title' | 'description' | 'profile' | 'comment' | 'company' | 'salary'>;
  @Input() actionLabel: string = "Edit";
  @Output() fieldEdited = new EventEmitter<Job>();

  @ContentChild('fieldDisplayContent', { static: false }) content: ElementRef | undefined;
  hasContent = false;

  formVisible = false;
  loading = false;
  form!: FormGroup;
  formFieldType = 'textarea';

  constructor(private fb: FormBuilder, private jobService: JobService, private notificationService: NotificationService) {}

  ngAfterContentInit() {
    this.hasContent = !!this.content;
  }

  ngOnInit(): void {
    this.initForm();
  }

  displayForm(): void {
    this.formVisible = true;
  }

  private initForm(): void {
    this.form = this.fb.group({});
  }

  submit(): void {
    if (this.form.valid) {
      console.log('isvalid');
      this.loading = true;
      this.jobService.updateJobField(this.job.id, this.form.value as UpdateJobFieldRequest).subscribe((job) => {
        this.loading = false;
        this.formVisible = false;
        const term = $localize `:@@info.job.updated:updated`;
        this.notificationService.confirmation($localize `:@@job.created_or_saved:Job ${term} successfully`);
        this.fieldEdited.emit(job);
      });
    }
  }

  cancel(): void {
    this.formVisible = false;
  }
}
