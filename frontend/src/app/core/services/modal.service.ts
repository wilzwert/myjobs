import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { JobEditionComponent } from '@features/jobs/job-editIion/job-edition.component';
import { Job } from '@core/model/job.interface';
import { JobStepperComponent } from '@features/jobs/job-stepper/job-stepper.component';
import { PasswordFormComponent } from '@features/user/password-form/password-form.component';
import { User } from '@core/model/user.interface';
import { ModalComponent } from '@layout/modal/modal.component';
import { ComponentInputData, ComponentInputDomainData } from '@core/model/component-input-data.interface';
import { UserEditComponent } from '@features/user/user-edit/user-edit.component';
import { CreateJobWithUrlFormComponent } from '@app/features/jobs/forms/create-job-with-url-form/create-job-with-url-form.component';

@Injectable({
  providedIn: 'root'
})
export class ModalService {

  private static MODAL_WIDTH = '80vw';
  private static MODAL_MAXWIDTH = '1000px';
  private static MODAL_OPTIONS = {
    width: ModalService.MODAL_WIDTH,
    maxWidth: ModalService.MODAL_MAXWIDTH,
  };

  constructor(private dialog: MatDialog) {}

  openCreateJobWithUrlModal(succeeded: (data: ComponentInputDomainData) => void): void {
    const componentInputData: ComponentInputData = { component: CreateJobWithUrlFormComponent, succeeded: succeeded, data: {}} as ComponentInputData;
    const dialogRef: MatDialogRef<ModalComponent> =  this.dialog.open(ModalComponent, {
      ...ModalService.MODAL_OPTIONS,
      data: componentInputData,
    });
  }

  openJobModal(type: 'create-job-with-url' | 'job' | 'attachments' | 'attachments-form' | 'activities' | 'activities-form', job: Job | null = null, succeeded: () => void, metadata = {}) {
    const componentInputData: ComponentInputData = { component: JobEditionComponent, succeeded: succeeded, data: {job: job, metadata: {...metadata, type: type}} as ComponentInputDomainData } as ComponentInputData
    const dialogRef: MatDialogRef<ModalComponent> =  this.dialog.open(ModalComponent, {
      ...ModalService.MODAL_OPTIONS,
      data: componentInputData,
    });
  }

  openJobStepperModal(succeeded: () => void, metadata = {}) {
    const componentInputData: ComponentInputData = { component: JobStepperComponent, succeeded: succeeded, data: { metadata: {...metadata}} } as ComponentInputData;
    const dialogRef: MatDialogRef<ModalComponent> =  this.dialog.open(ModalComponent, {
      ...ModalService.MODAL_OPTIONS,
      data: componentInputData
    });
  }

  openPasswordModal(succeeded: (data: ComponentInputDomainData) => void) {
    const componentInputData: ComponentInputData = { component: PasswordFormComponent, succeeded: succeeded } as ComponentInputData
    const dialogRef: MatDialogRef<ModalComponent> =  this.dialog.open(ModalComponent, {
      ...ModalService.MODAL_OPTIONS,
      data: componentInputData
    });
  }

  openUserEditModal(user: User, succeeded: (data: ComponentInputDomainData) => void) {
    const componentInputData: ComponentInputData = { component: UserEditComponent, succeeded: succeeded, data: {user: user} as ComponentInputDomainData } as ComponentInputData
    const dialogRef: MatDialogRef<ModalComponent> =  this.dialog.open(ModalComponent, {
      ...ModalService.MODAL_OPTIONS,
      data: componentInputData
    });
  }
}
