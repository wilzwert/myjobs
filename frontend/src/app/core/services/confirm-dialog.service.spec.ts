import { TestBed } from '@angular/core/testing';

import { ConfirmDialogService } from './confirm-dialog.service';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../features/dialogs/confirm-dialog/confirm-dialog.component';
import { of } from 'rxjs';

describe('ConfirmDialogService', () => {
  let confirmDialogService: ConfirmDialogService;
  let matDialogMock: jest.Mocked<MatDialog>;
  let matDialogRefMock: jest.Mocked<MatDialogRef<ConfirmDialogComponent>>;

  beforeEach(() => {
    matDialogMock = {
      open: jest.fn()
    } as unknown as jest.Mocked<MatDialog>;

    matDialogRefMock = {
      afterClosed: jest.fn()
    } as unknown as jest.Mocked<MatDialogRef<ConfirmDialogComponent>>;

  
    confirmDialogService = new ConfirmDialogService(matDialogMock);
    
  });

  it('should be created', () => {
    expect(confirmDialogService).toBeTruthy();
  });

  it('should open dialog with parameters', () => {
    const confirmFn = jest.fn();
    // we're testing the parameters used to open the dialog, not the confirm callback
    (matDialogRefMock.afterClosed as jest.Mock).mockReturnValue(of(false));
    matDialogMock.open.mockReturnValue(matDialogRefMock as MatDialogRef<any>);

    confirmDialogService.openConfirmDialog('Test message', confirmFn);

    expect(matDialogMock.open).toHaveBeenCalledTimes(1);
    expect(matDialogMock.open).toHaveBeenCalledWith(
      ConfirmDialogComponent,
      {
        width: '80vw',
      maxWidth: '1000px',
      data: { message: 'Test message', confirm: confirmFn }
      }
    )
  })

  it('should call confirm callback when result is true', () => {
    const confirmFn = jest.fn();
    (matDialogRefMock.afterClosed as jest.Mock).mockReturnValue(of(true));
    matDialogMock.open.mockReturnValue(matDialogRefMock as MatDialogRef<any>);

    confirmDialogService.openConfirmDialog('Test message', confirmFn);

    expect(confirmFn).toHaveBeenCalledTimes(1);
  })

  it('should not call confirm callback when result is false', () => {
    const confirmFn = jest.fn();
    (matDialogRefMock.afterClosed as jest.Mock).mockReturnValue(of(false));
    matDialogMock.open.mockReturnValue(matDialogRefMock as MatDialogRef<any>);

    confirmDialogService.openConfirmDialog('Test message', confirmFn);
    
    expect(confirmFn).not.toHaveBeenCalled();
  })

});
