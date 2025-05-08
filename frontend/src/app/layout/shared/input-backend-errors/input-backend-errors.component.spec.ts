import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InputBackendErrorsComponent } from './input-backend-errors.component';

describe('FieldBackendErrorsComponent', () => {
  let component: InputBackendErrorsComponent;
  let fixture: ComponentFixture<InputBackendErrorsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InputBackendErrorsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InputBackendErrorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
