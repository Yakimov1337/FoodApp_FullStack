import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { ToastrService } from 'ngx-toastr';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  constructor(private authService: AuthService, private router: Router,private toastr: ToastrService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Add the access token to the headers
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && !this.isRefreshing) {
          this.isRefreshing = true;
          return this.authService.refreshToken().pipe(
            switchMap((newAccessToken: string) => {
              localStorage.setItem('accessToken', newAccessToken);
              this.isRefreshing = false; // Reset the flag
              // Retry the failed request with the new token
              request = request.clone({
                setHeaders: {
                  Authorization: `Bearer ${newAccessToken}`,
                },
              });
              return next.handle(request);
            }),
            catchError((err) => {
              this.isRefreshing = false; // Reset the flag
              if (err.status === 403 || err.status === 401) { // Handle the custom status code
                this.toastr.info("Session expired. Please log in again.");
              }
              localStorage.removeItem('accessToken');
              sessionStorage.clear();
              // If refresh token fails, redirect to sign-in page
              this.router.navigate(['/auth/sign-in']);
              return throwError(() => new Error('Session expired. Please log in again.'));
            }),
          );
        } else {
          this.isRefreshing = false; // Reset the flag
          return throwError(() => error);
        }
      }),
    );
  }
}
