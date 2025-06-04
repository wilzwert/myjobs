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

@Component({
  selector: 'app-job-editable-field',
  imports: [MatIcon, MatButton, MatTooltip, ReactiveFormsModule, CommentInputComponent, DescriptionInputComponent, ProfileInputComponent, TitleInputComponent, CompanyInputComponent],
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

  protected formVisible = false;
  protected loading = false;
  protected form!: FormGroup;
  protected formFieldType = 'textarea';

  constructor(private fb: FormBuilder, private jobService: JobService) {}

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
    console.log(this.form?.value);
  }

  cancel(): void {
    this.formVisible = false;
  }

}
