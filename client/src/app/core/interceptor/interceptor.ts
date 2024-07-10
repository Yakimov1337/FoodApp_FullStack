import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, EMPTY, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthStateService } from '../../services/auth-state.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authStateService: AuthStateService, private router: Router) {}

  // This interceptor goal is to logout the user if there are no cookies, because ngrx user state won't notice cookie deletion
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Check if the request is for static assets and bypass the auth check
    // this causes unexpected behavior for sign-up page
    if (request.url.startsWith('assets/')) {
      return next.handle(request);
    }

    // For Appwrite API requests, perform the authentication check
      return this.authStateService.isAuthenticated().pipe(
        switchMap(isAuthenticated => {
          if (!isAuthenticated) {
            console.log('User not authenticated, redirecting to sign-in page.');
            this.router.navigate(['/auth/sign-in']).then(() => {
              console.log('Redirected to sign-in page due to unauthenticated request attempt.');
            });
            return EMPTY;
          }
          return next.handle(request);
        }),
        catchError(error => {
          console.error('Error in authentication process:', error);
          return throwError(() => error);
        })
      );
  }
}
