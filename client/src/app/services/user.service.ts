import { Injectable } from '@angular/core';
import { BehaviorSubject, forkJoin, from, Observable, throwError } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { Query } from 'appwrite';
import { environment } from 'src/environments/environment.local';
import { databases } from '../core/lib/appwrite';
import { User } from '../core/models';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly databaseId = environment.appwriteDatabaseId;
  private readonly usersCollectionId = environment.userCollectionId;
  private userCreatedSource = new BehaviorSubject<User | null>(null);
  private userUpdatedSource = new BehaviorSubject<User | null>(null);
  private userDeletedSource = new BehaviorSubject<string | undefined | null>(null);

  // Observable stream to be consumed by components
  userCreated$ = this.userCreatedSource.asObservable();
  userUpdated$ = this.userUpdatedSource.asObservable();
  userDeleted$ = this.userDeletedSource.asObservable();

  constructor() {}
  // Emit event when a user is created
  userCreated(user: User): void {
    this.userCreatedSource.next(user);
  }

  // Emit event when a user is updated
  userUpdated(user: User): void {
    this.userUpdatedSource.next(user);
  }

  // Emit event when a user is deleted
  userDeleted(userId: string | undefined | null): void {
    this.userDeletedSource.next(userId);
  }

  // Create USER is delegated to Auth service because we need to create Appwrite account too!

  getAllUsers(page: number = 1, limit: number = 10): Observable<User[]> {
    const offset = (page - 1) * limit;
    return from(
      databases.listDocuments(this.databaseId, this.usersCollectionId, [
        Query.limit(limit),
        Query.offset(offset),
        Query.orderDesc('$createdAt'),
      ]),
    ).pipe(
      map((result) => result.documents as unknown as User[]),
      catchError((error) => {
        console.error('Error fetching users:', error);
        return throwError(() => error);
      }),
    );
  }

  getUserById(userId: string): Observable<User> {
    return from(databases.getDocument(this.databaseId, this.usersCollectionId, userId)).pipe(
      map((result) => result as unknown as User),
      catchError((error) => {
        console.error('Error fetching user:', error);
        return throwError(() => error);
      }),
    );
  }

  updateUser(userId: string, userData: Partial<User>): Observable<User> {
    return from(databases.updateDocument(this.databaseId, this.usersCollectionId, userId, userData)).pipe(
      map((result) => result as unknown as User),
      catchError((error) => {
        console.error('Error updating user:', error);
        return throwError(() => error);
      }),
    );
  }

  //Deleting user from both Appwrite Auth table and Custom Users table
  deleteUser(documentId: string): Observable<any> {
    // Step 1: Fetch the user document to get the accountId (Appwrite Auth ID)
    return from(databases.getDocument(this.databaseId, this.usersCollectionId, documentId)).pipe(
      switchMap((userDocument) => {
        const accountId = userDocument['accountId'];

        // Step 2: Delete the user from Appwrite Auth system using accountId
        // const deleteAuthUser = from(account.deleteIdentity('65f8c54fd490d9cb9031'));

        // Step 3: Delete the user document from my database
        const deleteUserDocument = from(databases.deleteDocument(this.databaseId, this.usersCollectionId, documentId));

        // Step 4: Execute both deletions in parallel and wait for both to complete
        return forkJoin([deleteUserDocument]); //disabled deleteAuthUser for now
      }),
      catchError((error) => {
        console.error('Error deleting user:', error);
        return throwError(() => error);
      }),
    );
  }
}
