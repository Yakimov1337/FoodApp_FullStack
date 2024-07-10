import { Component } from '@angular/core';
import { ThemeService } from './services/theme.service';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { CommonModule, NgClass } from '@angular/common';
import { Store } from '@ngrx/store';
import { Observable, filter } from 'rxjs';
import { selectIsLoading } from './core/state/auth/auth.selectors';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: true,
  imports: [NgClass, RouterOutlet, CommonModule],
})
export class AppComponent {
  title = 'Food Squad';
  isLoading$: Observable<boolean>;

  constructor(public themeService: ThemeService, private store: Store, private router: Router) {
    this.isLoading$ = this.store.select(selectIsLoading);
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      window.scrollTo(0, 0);
    });
  }


}
