import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Job } from '@core/model/job.interface';
import { JobService } from '@core/services/job.service';
import { Attachment } from '@core/model/attachment.interface';
import { of, switchMap, take, tap } from 'rxjs';
import { ConfirmDialogService } from '@core/services/confirm-dialog.service';
import { FileService } from '@core/services/file.service';
import { MatButton } from '@angular/material/button';
import { JobAttachmentsFormComponent } from '@features/jobs/job-attachments-form/job-attachments-form.component';
import { ModalService } from '@core/services/modal.service';
import { DatePipe } from '@angular/common';
import { ProtectedFile } from '@app/core/model/protected-file.interface';

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

  constructor(private jobService: JobService, private confirmDialogService: ConfirmDialogService, private fileService: FileService, private modalService: ModalService){}

  ngOnInit(): void {
    this.displayForm = this.formMode === 'inline';
  }

  downloadAttachement(job: Job, attachment: Attachment) :void {

    this.jobService.getProtectedFile(job.id, attachment.id).
      pipe(
        switchMap((p: ProtectedFile) => {
          this.fileService.downloadFile(p.url, true).subscribe((blob) => {
            const a = document.createElement('a');
            const objectUrl = URL.createObjectURL(blob);
            window.open(objectUrl, '_blank');
            URL.revokeObjectURL(objectUrl)
          })
          return of(p);
        })
      ).
      subscribe()
    
    /*
    this.fileService.downloadFile(`/api/jobs/${job.id}/attachments/${attachment.id}/file`).subscribe((blob) => {
      const a = document.createElement('a');
      const objectUrl = URL.createObjectURL(blob);
      window.open(objectUrl, '_blank');
      URL.revokeObjectURL(objectUrl);
    });*/
  }

  confirmDeleteAttachment(job: Job, attachment: Attachment) :void {
    this.jobService.deleteAttachment(job.id, attachment.id).pipe(
      take(1),
      tap(() => job.attachments = job.attachments.filter((a) => a.id != attachment.id ))
    ).subscribe();
  }

 deleteAttachment(job: Job, attachment: Attachment) :void {
    this.confirmDialogService.openConfirmDialog(`Delete attachment "${attachment.name}" ?`, () => this.confirmDeleteAttachment(job, attachment));
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
