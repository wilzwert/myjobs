import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobEditionComponent } from './job-edition.component';
import { ComponentInputDomainData } from '@app/core/model/component-input-data.interface';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('JobEditionComponent', () => {
  let component: JobEditionComponent;
  let fixture: ComponentFixture<JobEditionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobEditionComponent],
      providers: [
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobEditionComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {   
    component.data = {
      job: {id: '1', title: 'Test Job', activities: [], url: '', attachments: [], company: '', description: '', comment: '', profile: '', salary: '', createdAt: '', rating: {value: 3}, status: 'PENDING', updatedAt: '', statusUpdatedAt: ''},
      metadata: {type: 'job'},
    } as ComponentInputDomainData;
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(component.data).toBeDefined();
  });
});
