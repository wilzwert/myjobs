import { Component, OnDestroy, OnInit } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { MenuComponent } from './layout/menu/menu.component';
import { NotificationComponent } from './layout/notification/notification.component';
import { filter, Subject, takeUntil } from 'rxjs';
import { MatMenuModule } from '@angular/material/menu';

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, MenuComponent, NotificationComponent, MatMenuModule],
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit, OnDestroy {
  public showMainMenu = true;

  private destroy$: Subject<boolean> = new Subject<boolean>();

  title = 'MyJobs';

  constructor(private router: Router) {}

  public ngOnInit() :void {
    this.router.events
    .pipe(
      // unsubscribe on component destruction
      takeUntil(this.destroy$), 
      filter(event => event instanceof NavigationEnd)
    )
    .subscribe(() => {
      // main menu must not be shown on homepage
      this.showMainMenu = !this.router.url.match(/^\/$/);
    });
  }

  public ngOnDestroy(): void {
    // emit to Subject to unsubscribe from observables
    this.destroy$.next(true);
  }
}
