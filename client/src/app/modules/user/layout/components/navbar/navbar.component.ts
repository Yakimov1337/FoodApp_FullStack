import { CommonModule } from '@angular/common';
import { Component, HostListener } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet, Event, RouterLink } from '@angular/router';
import { ProfileMenuComponent } from './profile-menu/profile-menu.component';
import { Observable } from 'rxjs';
import { User } from '../../../../../core/models';
import { Store, select } from '@ngrx/store';
import { selectCurrentUser } from '../../../../../core/state/auth/auth.selectors';
import { CartVisibilityService } from '../../../../../services/cart-visibility.service';
import { selectCartItemCount } from '../../../../../core/state/shopping-cart/cart.selectors';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterOutlet, CommonModule, ProfileMenuComponent, RouterLink],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  private mainContent: HTMLElement | null = null;
  isScrolled = false;
  mobileMenuOpen = false;
  cartItemCount$: Observable<number>;
  user$: Observable<User | null>;

  constructor(private router: Router, private store: Store, private cartVisibilityService: CartVisibilityService) {
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        if (this.mainContent) {
          this.mainContent!.scrollTop = 0;
        }
      }
    });
    this.user$ = this.store.pipe(select(selectCurrentUser));
    this.cartItemCount$ = this.store.select(selectCartItemCount);
  }

  ngOnInit(): void {
    this.mainContent = document.getElementById('main-content');
  }

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.isScrolled = window.scrollY > 0;
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  isMobileMenuVisible(): boolean {
    return this.mobileMenuOpen;
  }

  toggleCart(): void {
    this.cartVisibilityService.toggleCart();
  }
}
