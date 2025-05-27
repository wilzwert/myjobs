import { Component, OnDestroy, OnInit } from '@angular/core';
import { AsyncPipe, CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { SessionService } from '../../core/services/session.service';
import { catchError, filter, Observable, Subject, takeUntil, throwError } from 'rxjs';
import { NavigationEnd, NavigationSkipped, Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { LocaleService } from '../../core/services/locale.service';
import { MatMenu, MatMenuModule } from '@angular/material/menu';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [MatToolbarModule, MatMenu, MatMenuModule, MatIconModule, AsyncPipe, RouterLink, CommonModule],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit, OnDestroy {

  private destroy$: Subject<boolean> = new Subject<boolean>();
  public menuOpen: boolean = false;
  

  constructor(
    private sessionService: SessionService,
    private authService: AuthService,
    private router: Router,
    private localeService: LocaleService
  ) {}

  get lang() {
    return this.localeService.currentLocale;
  }
  
  public $isLogged(): Observable<boolean> {
    return this.sessionService.$isLogged();
  }

  public openNav() :void {
    this.menuOpen = true;
  }

  public closeNav() :void {
    this.menuOpen = false;
  }

  ngOnInit(): void {

    this.router.events.pipe(
      takeUntil(this.destroy$),
      filter(event => event instanceof NavigationEnd || event instanceof NavigationSkipped)
    )
      .subscribe(() => {
        this.closeNav();
    });
  }

  public ngOnDestroy(): void {
    // emit to Subject to unsubscribe from observables
    this.destroy$.next(true);
  }

  public changeLang(lang: 'fr' | 'en'): void {
    this.localeService.changeLocale(lang);
  }

  public logout(): void {
    this.authService.logout()
        .subscribe(
          () => {
            this.sessionService.logOut();
            this.router.navigate([''])
          }
        )
  }
}
