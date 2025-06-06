import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { JobService } from '@core/services/job.service';
import { CreateJobAttachmentsRequest } from '@core/model/create-job-attachments-request.interface';
import { Job } from '@core/model/job.interface';
import { catchError, take } from 'rxjs';
import { ApiError } from '@core/errors/api-error';
import { CreateJobAttachmentRequest } from '@core/model/create-job-attachment-request.interface';
import { MatButton, MatIconButton } from '@angular/material/button';
import { NotificationService } from '@core/services/notification.service';
import { MatIcon } from '@angular/material/icon';
import { ErrorProcessorService } from '@core/services/error-processor.service';

@Component({
  selector: 'app-job-attachments-form',
  imports: [ReactiveFormsModule, MatFormField, MatInput, MatLabel, MatButton, MatIcon, MatIconButton],
  templateUrl: './job-attachments-form.component.html',
  styleUrl: './job-attachments-form.component.scss'
})
export class JobAttachmentsFormComponent implements OnInit {
  @Input({ required: true }) job!: Job;
  @Input() defaultAttachements = 0;
  @Output() attachmentsSaved = new EventEmitter<Job>();

  loading = false;
  attachmentForm!: FormGroup;
  maxFileSize = 2*1024*1024;

  constructor(
    private fb: FormBuilder, 
    private jobService: JobService, 
    private notificationService: NotificationService,
      private errorProcessorService: ErrorProcessorService) {
  }

  ngOnInit(): void {
    this.attachmentForm = this.fb.group({
      attachments: this.fb.array([])
    });
    
    for(let a=0; a<this.defaultAttachements; a++) {
      this.addAttachment();
    }
  }

  get attachments(): FormArray {
    return this.attachmentForm.get('attachments') as FormArray;
  }

  addAttachment(): void {
    const attachmentGroup = this.fb.group({
      name: ['', Validators.required],
      file: [null, Validators.required],
      filename: [],
      content: [],
      fileU: ['', Validators.required]
    });
    this.attachments.push(attachmentGroup);
  }

  removeAttachment(index: number): void {
    this.attachments.removeAt(index);
  }

  onFileChange(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
  
      if(file.size > this.maxFileSize) {
        input.value = '';
        alert($localize`:@@alert.file_too_big:File too big`);
      }
      else {
        // Lire le fichier et le convertir en Base64
        const reader = new FileReader();
        reader.onload = () => {
          const base64String = reader.result as string;
          this.attachments.at(index).patchValue({ filename: file.name, content: base64String, fileU: 'chosen' });
        };
        reader.readAsDataURL(file);
      }
    }
  }

  submit(): void {
    if (this.attachmentForm.valid) {
      this.loading = true;
      const attachments: {}[] = new Array();
      
      const formData = new FormData();
      this.attachments.controls.forEach((attachment, index) => {
        attachments[index] = {'name': attachment.value.name, 'content':  attachment.value.content, 'filename': attachment.value.filename} as CreateJobAttachmentRequest;
      });

      
      // send `formData` to backend
      this.jobService.createAttachments(this.job.id, {attachments: attachments} as CreateJobAttachmentsRequest).pipe(
            take(1),
            catchError(
              (error: ApiError) => {
                  this.loading = false;
                  // set an explicit error message
                  error.message = `Attachments could not be created.${error.message}`;
                  return this.errorProcessorService.processError(error);
              }
            )
          )
          .subscribe((job) => {
            this.loading = false;
            this.notificationService.confirmation(
              attachments.length > 1 ?
                $localize`:@@message.attachements.created:${attachments.length} attachments created successfully`
                : $localize`:@@message.attachement.created:Attachment created successfully`
            );
            this.attachmentsSaved.emit(job);
          });
    }
  }

}
