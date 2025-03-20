import { Routes } from '@angular/router';
import { HomeComponent } from './layout/home/home.component';
import { LoginComponent } from './features/login/login.component';
import { RegistrationComponent } from './features/registration/registration.component';
import { JobsComponent } from './features/jobs/jobs.component';
import { MeComponent } from './features/me/me.component';

export const routes: Routes = [
    { 
        path: '', 
        component: HomeComponent,
        title: 'MyJobs - home' 
    },
    { 
        path: 'login',
        // canActivate: [UnauthGuard], 
        component: LoginComponent,
        title: 'Login',
    },
    { 
        path: 'register',
        // canActivate: [UnauthGuard],
        component: RegistrationComponent, 
        title: 'Registration', 
    },
    { 
        path: 'me',
        // canActivate: [UnauthGuard],
        component: MeComponent, 
        title: 'My profile', 
    },
    { 
        path: 'jobs',
        // canActivate: [AuthGuard],
        component: JobsComponent, 
        title: 'My Jobs', 
    },
];
 