import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, tap } from 'rxjs/operators';
import * as AuthActions from './auth.actions';
import { AuthService } from '../../../services/auth.service';
import { ToastrService } from 'ngx-toastr';

@Injectable()
export class AuthEffects {
  constructor(
    private actions$: Actions,
    private authService: AuthService,
    private router: Router,
    private store: Store,
    private toastr: ToastrService,
  ) {}

  // AuthEffects
  login$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.login),
      mergeMap(() => this.authService.getCurrentUserSession()),
      map((dbUser) => AuthActions.loginSuccess({ user: dbUser })),
      catchError((error) => of(AuthActions.loginFailure({ error: error.message }))),
    ),
  );
  loginSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.loginSuccess),
        tap(({ user }) => {
          // Handle navigation or other actions on success
          this.toastr.success(`Successfully logged in!`);
          if (user.role === 'Admin' || user.role === 'Moderator') {
            console.log(user.role);
            this.router.navigate(['/admin/dashboard']);
          } else {
            this.router.navigate(['/']);
          }
        }),
      ),
    { dispatch: false },
  );
  restoreSessionSuccess$ = createEffect(() => this.actions$.pipe(ofType(AuthActions.restoreSessionSuccess)), {
    dispatch: false,
  });
  logout$ = createEffect(() =>
  this.actions$.pipe(
      ofType(AuthActions.logout),
      tap(() => console.log('Logout action received')),
      switchMap(() => {
          console.log('Calling signOutAccount from AuthService');
          return this.authService.signOutAccount().pipe(
              tap(() => {
                  console.log('Successfully called signOutAccount');
                  // Client-side cleanup
                  localStorage.removeItem('cookieFallback');
                  sessionStorage.clear();
                  // Navigate to sign-in page
                  this.router.navigate(['/auth/sign-in']);
              }),
              map(() => {
                  console.log('Dispatching logoutSuccess');
                  return AuthActions.logoutSuccess();
              }),
              catchError((error) => {
                  console.error('Error during logout:', error);
                  return of(AuthActions.logoutFailure({ error: error.toString() }));
              })
          );
      }),
      tap(() => console.log('Logout effect complete')),
  ),
  { dispatch: false },
);
}
