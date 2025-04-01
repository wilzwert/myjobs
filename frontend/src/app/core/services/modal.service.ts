import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { JobEditionComponent } from '../../features/jobs/job-editIion/job-edition.component';
import { Job } from '../model/job.interface';
import { JobStepperComponent } from '../../features/jobs/job-stepper/job-stepper.component';
import { PasswordFormComponent } from '../../features/user/password-form/password-form.component';
import { UserFormComponent } from '../../features/user/user-form/user-form.component';
import { User } from '../model/user.interface';
import { ModalComponent } from '../../layout/modal/modal.component';
import { ComponentInputData, ComponentInputDomainData } from '../model/component-input-data.interface';

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

  openJobModal(type: 'job' | 'attachments' | 'activity', job: Job | null = null, succeeded: () => void) {
    const componentInputData: ComponentInputData = { component: JobEditionComponent, succeeded: succeeded, data: {job: job, metadata: {type: type}} as ComponentInputDomainData } as ComponentInputData
    const dialogRef: MatDialogRef<ModalComponent> =  this.dialog.open(ModalComponent, {
      ...ModalService.MODAL_OPTIONS,
      data: componentInputData,
    });
  }

  openJobStepperModal(succeeded: () => void) {
    const componentInputData: ComponentInputData = { component: JobStepperComponent, succeeded: succeeded } as ComponentInputData;
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
    const componentInputData: ComponentInputData = { component: UserFormComponent, succeeded: succeeded, data: {user: user} as ComponentInputDomainData } as ComponentInputData
    const dialogRef: MatDialogRef<ModalComponent> =  this.dialog.open(ModalComponent, {
      ...ModalService.MODAL_OPTIONS,
      data: componentInputData
    });
  }
}
