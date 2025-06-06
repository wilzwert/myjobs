import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Job } from '@core/model/job.interface';
import { JobService } from '@core/services/job.service';
import { Attachment } from '@core/model/attachment.interface';
import { catchError, EMPTY, of, switchMap, take, tap } from 'rxjs';
import { ConfirmDialogService } from '@core/services/confirm-dialog.service';
import { FileService } from '@core/services/file.service';
import { MatButton } from '@angular/material/button';
import { JobAttachmentsFormComponent } from '@app/features/jobs/forms/job-attachments-form/job-attachments-form.component';
import { ModalService } from '@core/services/modal.service';
import { DatePipe } from '@angular/common';
import { ProtectedFile } from '@app/core/model/protected-file.interface';
import { NotificationService } from '@app/core/services/notification.service';

@Component({
  selector: 'app-job-attachments',
  imports: [MatButton, JobAttachmentsFormComponent, DatePipe],
  templateUrl: './job-attachments.component.html',
  styleUrl: './job-attachments.component.scss'
})
export class JobAttachmentsComponent implements OnInit {
  @Input({ required: true }) job!: Job;
  @Input() formMode = 'inline';
  @Output() attachmentsSaved = new EventEmitter<Job>();

  protected displayForm = this.formMode === 'inline';

  constructor(
    private jobService: JobService, 
    private confirmDialogService: ConfirmDialogService, 
    private fileService: FileService, 
    private modalService: ModalService, 
    private notificationService: NotificationService){}

  ngOnInit(): void {
    this.displayForm = this.formMode === 'inline';
  }

  downloadAttachement(job: Job, attachment: Attachment) :void {
    this.jobService.getProtectedFile(job.id, attachment.id)
      .pipe(
        switchMap((p: ProtectedFile) =>
          this.fileService.downloadFile(p.url, true).pipe(
            tap((blob) => {
              const objectUrl = URL.createObjectURL(blob);
              window.open(objectUrl, '_blank');
              URL.revokeObjectURL(objectUrl);
            })
          )
        ),
      catchError((err) => {
        this.notificationService.error($localize `:download file|message indicating file download has failed@@error.file.download:File download failed`, err);
        return EMPTY;
      })
  )
  .subscribe();
  }

  confirmDeleteAttachment(job: Job, attachment: Attachment) :void {
    console.log('deleting attachment');
    this.jobService.deleteAttachment(job.id, attachment.id).pipe(
      take(1),
      tap(() => {
        this.notificationService.confirmation($localize `:@@message.attachment.deleted:Attachment deleted successfully`);
        console.log('filtering attachments');
        job.attachments = job.attachments.filter((a) => a.id != attachment.id )
    })
    ).subscribe(() => {
      console.log('something happened');
    });
  }

 deleteAttachment(job: Job, attachment: Attachment) :void {
    this.confirmDialogService.openConfirmDialog($localize `:@@info.attachment.delete.confirmation_required:Delete attachment "${attachment.name}" ?`, () => this.confirmDeleteAttachment(job, attachment));
  }

  cancelForm() :void {
    this.displayForm = false;
  } 

 addAttachment(job: Job) :void {
  if(this.formMode === 'inline') {
    this.displayForm = true;
  }
  else {
    this.modalService.openJobModal('attachments-form', job, () => this.onAttachmentsSaved(job), {defaultAttachments: 1});
  }
 }

 onAttachmentsSaved(job: Job): void {
  this.attachmentsSaved.emit(job);
 }

}
