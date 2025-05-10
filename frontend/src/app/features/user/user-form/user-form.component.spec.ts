import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserFormComponent } from './user-form.component';
import { ReactiveFormsModule, FormGroup, FormControl } from '@angular/forms';
import { LocaleService } from '../../../core/services/locale.service';
import { By, DomSanitizer } from '@angular/platform-browser';
import { of } from 'rxjs';
import { MatIconRegistry } from '@angular/material/icon';

describe('UserFormComponent', () => {
  let component: UserFormComponent;
  let fixture: ComponentFixture<UserFormComponent>;
  let localeServiceMock: Partial<LocaleService>;

  beforeEach(async () => {
    localeServiceMock = { currentLocale: 'fr' };

    const matIconRegistryMock = {
      addSvgIcon: () => matIconRegistryMock,
      getNamedSvgIcon: () => of(''), // if needed
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, UserFormComponent],
      providers: [
        { provide: LocaleService, useValue: localeServiceMock },
        { provide: MatIconRegistry, useValue: {addSvgIcon: () => matIconRegistryMock, getNamedSvgIcon: () => of('')} },
        { provide: DomSanitizer, useValue: { bypassSecurityTrustResourceUrl: (url: string) => url } },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserFormComponent);
    component = fixture.componentInstance;

    component.form = new FormGroup({
      email: new FormControl('test@example.com'),
      username: new FormControl('testuser'),
      firstName: new FormControl('John'),
      lastName: new FormControl('Doe'),
    });

    fixture.detectChanges();
  });

  it('should add lang control with current locale in uppercase', () => {
    expect(component.form.get('lang')?.value).toBe('FR');
    expect(component.lang).toBeDefined();
  });

  it('should emit submitted event on submit()', () => {
    jest.spyOn(component.submitted, 'emit');
    component.submit();
    expect(component.submitted.emit).toHaveBeenCalled();
  });

  it('should render submit button with provided label', () => {
    component.submitLabel = 'Register';
    fixture.detectChanges();

    const button = fixture.debugElement.query(By.css('button'));
    expect(button.nativeElement.textContent).toContain('Register');
  });

  it('should add password control when showPassword is true', () => {
    component.showPassword = true;
    component.ngOnInit(); 
    fixture.detectChanges();
    expect(component.form.get('password')).toBeTruthy();
    expect(component.password).toBeDefined();
  })

  it('should add jobFollowUpReminderDays control when showJobFollowUpReminderDays is true', () => {
    component.showJobFollowUpReminderDays = true;
    component.ngOnInit(); 
    fixture.detectChanges();
    expect(component.form.get('jobFollowUpReminderDays')).toBeTruthy();
    expect(component.jobFollowUpReminderDays).toBeDefined();
  })
});
