import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import * as AuthActions from '../core/state/auth/auth.actions';
import { Observable, map, of } from 'rxjs';
import { AuthService } from './auth.service';
import { User } from '../core/models';
import { selectIsAuthenticated } from '../core/state/auth/auth.selectors';

@Injectable({
  providedIn: 'root',
})
export class AuthStateService {
  constructor(private store: Store, private authService: AuthService) {}

  // In AuthStateService
  initializeAuthState(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!this.checkAuthFallback()) {
        resolve();
        return;
      }
      this.authService.getCurrentUserSession().subscribe({
        next: (user) => {
          if (user) {
            this.store.dispatch(AuthActions.restoreSessionSuccess({ user: user as unknown as User }));
          } else {
            // this.store.dispatch(AuthActions.logout());
          }
          resolve();
        },
        error: reject,
      });
    });
  }

  private checkAuthFallback(): boolean {
    const cookieFallback = localStorage.getItem('cookieFallback');
    return cookieFallback !== null && cookieFallback !== '[]';
  }

  // Checks if the user is authenticated
  isAuthenticated(): Observable<boolean> {
    // First, check if the token exists in localStorage
    const tokenExists = this.checkAuthFallback();

    if (!tokenExists) {
      // If the token doesn't exist, dispatch a logout action to update the state
      // this.store.dispatch(AuthActions.logout());
      return of(false);
    }

    // If the token exists, then proceed to check the NgRx store for the authentication state
    return this.store.select(selectIsAuthenticated);
  }

  hasAnyRole(expectedRoles: string[]): Observable<boolean> {
    return this.authService.getCurrentUserSession().pipe(map((user) => expectedRoles.includes(user?.role)));
  }
}
