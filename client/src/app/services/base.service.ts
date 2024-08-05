import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class BaseService {
  constructor(
    private http: HttpClient,
    private router: Router,
    private toastr: ToastrService
  ) {}

  protected get<T>(url: string, params?: HttpParams): Observable<T> {
    return this.http.get<T>(url, { params }).pipe(
      catchError(this.handleError.bind(this))
    );
  }

  protected post<T>(url: string, body: any): Observable<T> {
    return this.http.post<T>(url, body).pipe(
      catchError(this.handleError.bind(this))
    );
  }

  protected put<T>(url: string, body: any): Observable<T> {
    return this.http.put<T>(url, body).pipe(
      catchError(this.handleError.bind(this))
    );
  }

  protected delete<T>(url: string): Observable<T> {
    return this.http.delete<T>(url).pipe(
      catchError(this.handleError.bind(this))
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Server error';

    if (error.status === 401) {
      // Unauthorized error - handle token expiration
      errorMessage = 'Your session has expired. Please log in again.';
      this.router.navigate(['/auth/sign-in']);
    } else if (error.status === 500) {
      // Internal Server Error
      errorMessage = 'An unexpected error occurred on the server.';
    } else if (error.error && error.error.error) {
      // Other specific errors
      errorMessage = error.error.error;
    }
    return throwError(() => errorMessage);
  }
}
