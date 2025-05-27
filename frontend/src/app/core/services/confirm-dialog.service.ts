import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '@features/dialogs/confirm-dialog/confirm-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class ConfirmDialogService {

  constructor(private dialog: MatDialog) {}

  openConfirmDialog(message: String, confirm: () => void): void {
    const dialogRef: MatDialogRef<ConfirmDialogComponent> =  this.dialog.open(ConfirmDialogComponent, {
      width: '80vw',
      maxWidth: '1000px',
      data: { message, confirm }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if(result) {
        if(confirm) {
          confirm();
        }
      }
    })
  }
}