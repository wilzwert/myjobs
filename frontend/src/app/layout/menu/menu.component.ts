import { Component, OnDestroy, OnInit } from '@angular/core';
import { AsyncPipe, CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { SessionService } from '../../core/services/session.service';
import { filter, Observable, Subject, takeUntil } from 'rxjs';
import { NavigationEnd, NavigationSkipped, Route, Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { BreakpointObserver } from '@angular/cdk/layout';
import { LocaleService } from '../../core/services/locale.service';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [MatToolbarModule, MatIconModule, AsyncPipe, RouterLink, CommonModule],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit, OnDestroy {

  private destroy$: Subject<boolean> = new Subject<boolean>();
  public isMobile:boolean = true;
  public menuOpen: boolean = false;
  

  constructor(
    private sessionService: SessionService,
    private breakpointObserver: BreakpointObserver,
    private router: Router,
    private localeService: LocaleService) {}

  public $isLogged(): Observable<boolean> {
    return this.sessionService.$isLogged();
  }

  public openNav() :void {
    if(this.isMobile) {
      this.menuOpen = true;
    }
  }

  public closeNav() :void {
    if(this.isMobile) {
      this.menuOpen = false;
    }
  }

  ngOnInit(): void {
    this.breakpointObserver.observe(['(min-width:769px)'])
    .pipe(takeUntil(this.destroy$))
    .subscribe((screensize) => {
      if(screensize.matches) {
        this.isMobile = false;
      }
      else {
        this.isMobile = true;
      }
    });

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
}
