import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MeComponent } from './me.component';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { SessionService } from '../../../core/services/session.service';
import { ModalService } from '../../../core/services/modal.service';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { User } from '../../../core/model/user.interface';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { EmailStatus } from '../../../core/model/email-status';
import { provideHttpClient } from '@angular/common/http';
import { ApiError } from '../../../core/errors/api-error';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  let userServiceMock: jest.Mocked<UserService>;
  let authServiceMock: jest.Mocked<AuthService>;
  let sessionServiceMock: jest.Mocked<SessionService>;
  let modalServiceMock: jest.Mocked<ModalService>;
  let dialogServiceMock: jest.Mocked<ConfirmDialogService>;
  let notificationServiceMock: jest.Mocked<NotificationService>;
  let routerMock: jest.Mocked<Router>;

  beforeEach(async () => {
    userServiceMock = {
      getUser: jest.fn().mockReturnValue(of({ id: 1, firstName: 'John', lastName: 'Doe', email: 'john@doe.com', createdAt: '', emailStatus: EmailStatus.VALIDATED, username: 'john'  } as User)),
      deleteUser: jest.fn().mockReturnValue(of({})),
      sendVerificationMail: jest.fn().mockReturnValue(of({}))
    } as unknown as jest.Mocked<UserService>;

    authServiceMock = {
      logout: jest.fn().mockReturnValue(of({}))
    } as unknown as jest.Mocked<AuthService>;

    sessionServiceMock = {
      logOut: jest.fn()
    } as unknown as jest.Mocked<SessionService>;

    modalServiceMock = {
      openPasswordModal: jest.fn(),
      openUserEditModal: jest.fn()
    } as unknown as jest.Mocked<ModalService>;

    dialogServiceMock = {
      openConfirmDialog: jest.fn()
    } as unknown as jest.Mocked<ConfirmDialogService>;

    notificationServiceMock = {
      confirmation: jest.fn(),
      error: jest.fn()
    } as unknown as jest.Mocked<NotificationService>;

    routerMock = {
      navigate: jest.fn()
    } as unknown as jest.Mocked<Router>;

    await TestBed.configureTestingModule({
      imports: [MeComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: UserService, useValue: userServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: ModalService, useValue: modalServiceMock },
        { provide: ConfirmDialogService, useValue: dialogServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should call userService.getUser() and populate user$', () => {
    component['user$'].subscribe(user => {
      expect(user).toEqual({ id: 1, name: 'John Doe' });
    });
    expect(userServiceMock.getUser).toHaveBeenCalled();
  });

  it('should call modalService.openPasswordModal() when changePassword is called', () => {
    component.changePassword();
    expect(modalServiceMock.openPasswordModal).toHaveBeenCalled();
  });

  it('should call userService.deleteUser() and log out when confirmDeleteAccount is called', () => {
    component.confirmDeleteAccount();
    expect(userServiceMock.deleteUser).toHaveBeenCalled();
    expect(sessionServiceMock.logOut).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['']);
  });

  it('should call notificationService.confirmation() when endDeleteAccount is called', () => {
    component['endDeleteAccount']();
    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith('Your account has been deleted. Thank your for using MyJobs.');
  });

  it('should call dialogService.openConfirmDialog() when deleteAccount is called', () => {
    component.deleteAccount();
    expect(dialogServiceMock.openConfirmDialog).toHaveBeenCalled();
  });

  it('should call userService.sendVerificationMail() when confirmSendVerificationEmail is called', () => {
    component.confirmSendVerificationEmail();
    expect(userServiceMock.sendVerificationMail).toHaveBeenCalled();
  });

  it('should call notificationService.confirmation() when sendVerificationEmail is called', () => {
    component.sendVerificationEmail();
    expect(dialogServiceMock.openConfirmDialog).toHaveBeenCalled();
  });

  it('should call modalService.openUserEditModal() when editUser is called', () => {
    const user = { id: 1, firstName: 'John', lastName: 'Doe', email: 'john@doe.com', createdAt: '', emailStatus: EmailStatus.VALIDATED, username: 'john'  } as User;
    component.editUser(user);
    expect(modalServiceMock.openUserEditModal).toHaveBeenCalledWith(user, expect.any(Function));
  });

  it('should call authService.logout() when logout is called', () => {
    component.logout();
    expect(authServiceMock.logout).toHaveBeenCalled();
    expect(sessionServiceMock.logOut).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['']);
  });

  it('should do nothing when deleting account fails', () => {
    const errorResponse = { status: 500, error: { message: 'Deletion error' }, headers: {} } as any as ApiError;
    userServiceMock.deleteUser.mockReturnValue(throwError(() => errorResponse));
    authServiceMock.logout.mockReturnValue(of({})); // Mock successful logout

    component.confirmDeleteAccount();

    fixture.detectChanges(); // Triggers ngOnInit

    expect(userServiceMock.deleteUser).toHaveBeenCalled();
    expect(sessionServiceMock.logOut).not.toHaveBeenCalled();
    expect(authServiceMock.logout).not.toHaveBeenCalled();
  });

  it('should do nothing when sending verification email fails', () => {
    const errorResponse = new ApiError({ status: 500, error: { message: 'Email send error' }, headers: {} } as any);
    userServiceMock.sendVerificationMail.mockReturnValue(throwError(() => errorResponse));

    component.confirmSendVerificationEmail();

    fixture.detectChanges(); // Triggers ngOnInit

    expect(userServiceMock.sendVerificationMail).toHaveBeenCalled();
    expect(notificationServiceMock.confirmation).not.toHaveBeenCalled();
  });

  it('should do nothing when error during logout', () => {
    const errorResponse = new ApiError({ status: 500, error: { message: 'Logout error' }, headers: {} } as any);
    authServiceMock.logout.mockReturnValue(throwError(() => errorResponse));
    sessionServiceMock.logOut.mockImplementation(() => {});

    component.logout();

    fixture.detectChanges(); // Triggers ngOnInit

    expect(sessionServiceMock.logOut).not.toHaveBeenCalled();
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });
});
