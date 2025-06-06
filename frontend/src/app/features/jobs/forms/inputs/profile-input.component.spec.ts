import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormGroup } from '@angular/forms';
import { TINYMCE_SCRIPT_SRC } from '@tinymce/tinymce-angular';
import { By } from '@angular/platform-browser';
import { DescriptionInputComponent } from './description-input.component';
import { ProfileInputComponent } from './profile-input.component';

describe('CommentInputComponent', () => {
  let component: ProfileInputComponent;
  let fixture: ComponentFixture<ProfileInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [{ provide: TINYMCE_SCRIPT_SRC, useValue: 'tinymce/tinymce.min.js' }]
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileInputComponent);
    component = fixture.componentInstance;

    // Création d'un FormGroup vide, on l'injecte
    component.form = new FormGroup({});
    component.initialValue = 'Initial comment';

    fixture.detectChanges();
  });

  it('should create control on ngOnInit via configure()', () => {
    const control = component.control;
    expect(control).toBeDefined();
    expect(control?.value).toBe('Initial comment');
  });

  it('should render textarea bound to comment form control', () => {
    const textarea = fixture.debugElement.query(By.css('textarea'));
    expect(textarea).toBeTruthy();
    expect(textarea.nativeElement.value).toBe('Initial comment');
  });

  it('should update textarea value when form control changes', () => {
    component.control.setValue('Changed content');
    fixture.detectChanges();

    const textarea = fixture.debugElement.query(By.css('textarea'));
    expect(textarea.nativeElement.value).toBe('Changed content');
  });

  it('should update control value on updateRichText', () => {
    const mockEvent = {
      editor: {
        getContent: () => '<p>Updated content</p>'
      }
    };

    const result = component.updateRichText(mockEvent);
    expect(result).toBe(true);
    expect(component.control.value).toBe('<p>Updated content</p>');
  });
});