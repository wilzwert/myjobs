import { Routes } from '@angular/router';
import { HomeComponent } from './layout/home/home.component';
import { LoginComponent } from './features/login/login.component';
import { RegistrationComponent } from './features/registration/registration.component';
import { JobsComponent } from './features/jobs/list/jobs.component';
import { MeComponent } from './features/user/me/me.component';
import { JobDetailComponent } from './features/jobs/job-detail/job-detail.component';
import { UnauthGuard } from './core/guards/unauth.guard';
import { AuthGuard } from './core/guards/auth.guard';
import { ResetPasswordComponent } from './features/user/password/reset-password/reset-password.component';
import { NewPasswordComponent } from './features/user/password/new-password/new-password.component';
import { EmailValidationComponent } from './features/user/email-validation/email-validation.component';
import { LanguageRedirectGuard } from './core/guards/language-redirect.guard';

export const routes: Routes = [
    {
        path: '',
        canActivate: [LanguageRedirectGuard],
        children: [
            { 
                path: '', 
                component: HomeComponent,
                title: 'MyJobs - home' 
            },
            { 
                path: 'login',
                canActivate: [UnauthGuard], 
                component: LoginComponent,
                title: 'Login',
            },
            { 
                path: 'register',
                canActivate: [UnauthGuard],
                component: RegistrationComponent, 
                title: 'Registration', 
            },
            { 
                path: 'password/reset',
                canActivate: [UnauthGuard],
                component: ResetPasswordComponent, 
                title: 'Reset password', 
            },
            { 
                path: 'password/new',
                canActivate: [UnauthGuard],
                component: NewPasswordComponent, 
                title: 'Create new password', 
            },
            { 
                path: 'me',
                canActivate: [AuthGuard],
                component: MeComponent, 
                title: 'My profile', 
            },
            { 
                path: 'me/email/validation',
                component: EmailValidationComponent, 
                title: 'Email validation', 
            },
            { 
                path: 'jobs',
                canActivate: [AuthGuard],
                component: JobsComponent, 
                title: 'My Jobs', 
            },
            { 
                path: 'jobs/:id', 
                canActivate: [AuthGuard],
                component: JobDetailComponent, 
                title: 'Job details',
                data: {goBackToRoute: "jobs"} 
            },
        ]
    }
];