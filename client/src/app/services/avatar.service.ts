import { Injectable } from '@angular/core';

import { environment } from 'src/environments/environment.local';
import { ToastrService } from 'ngx-toastr';
import { ID, databases, storage } from '../core/lib/appwrite';
import { Observable, catchError, from, lastValueFrom, map, throwError } from 'rxjs';
import { User } from '../core/models';

@Injectable({
  providedIn: 'root',
})
export class AvatarService {
  private readonly usersCollectionId = environment.userCollectionId;
  private readonly databaseId = environment.appwriteDatabaseId;

  constructor(private toastr: ToastrService) {}

  async uploadAvatar(file: File, userId: string): Promise<void> {
    try {
      // Upload the file
      const result = await storage.createFile(environment.appwriteStorageId, ID.unique(), file);

      const avatarUrl = `${environment.appwriteEndpoint}/storage/buckets/${environment.appwriteStorageId}/files/${result.$id}/view?project=${environment.appwriteProjectId}&mode=admin`;

      // Wait for the updateUserProfile Observable to complete
      await lastValueFrom(this.updateUserProfile(userId, avatarUrl));

      this.toastr.success('Avatar updated successfully');
    } catch (error) {
      console.error('Error uploading avatar:', error);
      this.toastr.error('Failed to update avatar');
    }
  }

  updateUserProfile(userId: string, avatarUrl: string): Observable<User> {
    return from(
      databases.updateDocument(this.databaseId, this.usersCollectionId, userId, { imageUrl: avatarUrl }),
    ).pipe(
      map((result) => result as unknown as User),
      catchError((error) => {
        console.error('Error updating user profile:', error);
        this.toastr.error('Failed to update avatar');
        return throwError(() => error);
      }),
    );
  }

  // Method to set default avatar based on user's email
  async setDefaultAvatar(userId: string, userEmail: string): Promise<void> {
    try {
      const initials = userEmail.split('@')[0].replace('.', '%'); // Customize as needed
      const defaultAvatarUrl = `${environment.appwriteEndpoint}/avatars/initials?name=${initials}&project=${environment.appwriteProjectId}`;

      // Await for the updateUserProfile Observable to complete
      await lastValueFrom(this.updateUserProfile(userId, defaultAvatarUrl));

      this.toastr.success('Default avatar set successfully');
      return Promise.resolve();
    } catch (error) {
      console.error('Error setting default avatar:', error);
      this.toastr.error('Failed to set default avatar');
      return Promise.reject(error);
    }
  }
}
