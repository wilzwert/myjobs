import { environment } from '@environments/environment';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideScReCaptchaSettings } from '@semantic-components/re-captcha';
import { Router, RouterModule } from '@angular/router';
import { HomeComponent } from './layout/home/home.component';

describe('AppComponent', () => {

let fixture: ComponentFixture<AppComponent>;
let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppComponent,
        RouterModule.forRoot([{path: '', component: HomeComponent}]),
      ],
     providers: [
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
        provideScReCaptchaSettings({
          v3SiteKey: environment.recaptcha_key,
          languageCode: 'fr',
        }),
     ]
    }).compileComponents();
    router = TestBed.inject(Router);
    
    fixture = TestBed.createComponent(AppComponent);
    router.initialNavigation(); // 👈 pour déclencher la navigation
    fixture.detectChanges();    // 👈 pour initialiser les composants
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have the 'MyJobs' title`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('MyJobs');
  });

  it('should render title', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('MyJobs');
  });
});
