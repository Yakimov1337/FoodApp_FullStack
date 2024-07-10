import { Injectable } from '@angular/core';
import { from, Observable, throwError } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { account, avatars, databases, ID } from '../core/lib/appwrite';
import { environment } from 'src/environments/environment.local';
import { User, Role } from '../core/models/user.model';
import { isValidUrl } from '../shared/validators/url-validator';
import { Query } from 'appwrite';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly databaseId = environment.appwriteDatabaseId;
  private readonly usersCollectionId = environment.userCollectionId;

  constructor(private router: Router, private toastr: ToastrService) {}

  // Create user account using Appwrite's SDK and save the user to the database
  createUserAccount(user: {
    email: string;
    password: string;
    name?: string;
    phoneNumber?: string;
    role?: Role;
    imageUrl?: URL;
  }): Observable<User> {
    return from(account.create(ID.unique(), user.email, user.password)).pipe(
      catchError((error) => {
        console.error('Error creating user account:', error);
        return throwError(() => error);
      }),
      switchMap((newAccount) => {
        // WAIT for saveUserToDB to complete
        if (!newAccount) throw new Error('Account creation failed');
        const avatarUrl = avatars.getInitials(user.email);

        const newUser: User = {
          accountId: newAccount.$id,
          name: user.name ?? '',
          phoneNumber: user.phoneNumber ?? '',
          email: newAccount.email,
          role: user.role ?? Role.Normal,
          imageUrl: user.imageUrl && isValidUrl(user.imageUrl) ? user.imageUrl : avatarUrl,
          orders: [],
        };

        return this.saveUserToDB(newUser); // Return the saveUserToDB observable
      }),
      catchError((error) => {
        console.error('Error saving user to database:', error);
        return throwError(() => error);
      }),
    );
  }
  // Save user details to the database
  private saveUserToDB(user: Omit<User, '$id'>): Observable<User> {
    return from(databases.createDocument(this.databaseId, this.usersCollectionId, ID.unique(), user)).pipe(
      catchError((error) => {
        console.error('Error saving user to database:', error);
        return throwError(() => error);
      }),
      map((newUser) => newUser as unknown as User),
    );
  }

  // ============================== GET ACCOUNT
  getCurrentUserSession(): Observable<User> {
    return from(account.get()).pipe(
      catchError((error) => {
        this.router.navigate(['/auth/sign-in']);
        localStorage.removeItem('cookieFallback');
        return throwError(() => new Error('Failed to get Appwrite account'));
      }),
      switchMap((currentAccount) => {
        if (!currentAccount) {
          return throwError(() => new Error('No current account found'));
        }
        // Now fetch the user details from your users collection
        return from(
          databases.listDocuments(environment.appwriteDatabaseId, environment.userCollectionId, [
            Query.equal('accountId', currentAccount.$id),
          ]),
        ).pipe(
          map((response) => {
            if (response.documents.length === 0) {
              throw new Error('User not found in custom users collection');
            }
            return response.documents[0] as unknown as User; // Returns custom users table instead of Appwrite user
          }),
          catchError((error) => {
            console.error('Error fetching user from users collection:', error);
            return throwError(() => new Error('Failed to fetch user from users collection'));
          }),
        );
      }),
    );
  }

  // Sign in account
  signInAccount(email: string, password: string): Observable<any> {
    return from(account.createEmailSession(email, password)).pipe(
        switchMap(session => {
            // Authentication was successful. Now, check if the user exists in the custom database.
            return this.checkUserExistsInDB(session.userId).pipe(
                map(user => {
                    // If this point is reached, the user exists in DB, and can proceed.
                    return user;
                }),
                catchError(() => {
                    // The user was not found in the custom database. Restrict the sign-in.
                    // This is triggered when user acc is deleted from users table but exists in Appwrite Auth table
                    return throwError(() => new Error('This account email is restricted from signing in.'));
                })
            );
        }),
        catchError(error => {
            return throwError(() => error);
        }),
    );
}
private checkUserExistsInDB(userId: string): Observable<User> {
  return from(databases.listDocuments(this.databaseId, this.usersCollectionId, [Query.equal('accountId', userId)])).pipe(
      map(response => {
          if (response.documents.length === 0) {
              throw new Error('User not found in database');
          }
          return response.documents[0] as unknown as User;
      })
  );
}
  // Sign out account
  signOutAccount(): Observable<any> {
    return from(account.deleteSession('current')).pipe(
      tap(() => console.log('Session deletion attempted.')),
      catchError((error) => {
        console.error('Error signing out:', error);
        return throwError(() => error);
      }),
    );
  }
}
