import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormGroup } from '@angular/forms';
import { TINYMCE_SCRIPT_SRC } from '@tinymce/tinymce-angular';
import { TitleInputComponent } from './title-input.component';

describe('SalaryInputComponent', () => {
  let component: TitleInputComponent;
  let fixture: ComponentFixture<TitleInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [{ provide: TINYMCE_SCRIPT_SRC, useValue: 'tinymce/tinymce.min.js' }]
    }).compileComponents();

    fixture = TestBed.createComponent(TitleInputComponent);
    component = fixture.componentInstance;

    // Création d'un FormGroup vide, on l'injecte
    component.form = new FormGroup({});
    component.initialValue = 'Initial salary';
  });

  it('should create control on ngOnInit via configure()', () => {
    component.ngOnInit();

    const control = component.control;
    expect(control).toBeDefined();
    expect(control?.value).toBe('Initial salary');
  });
});
