import { Injectable } from '@angular/core';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { databases, ID } from '../core/lib/appwrite';
import { MenuItem } from '../core/models';
import { environment } from 'src/environments/environment.local';
import { Query } from 'appwrite';

@Injectable({
  providedIn: 'root',
})
export class MenuItemsService {
  private readonly databaseId = environment.appwriteDatabaseId;
  private readonly MenuItemsCollectionId = environment.menuItemsCollectionId;
  private menuItemCreatedSource = new BehaviorSubject<MenuItem | null>(null);
  private menuItemUpdatedSource = new BehaviorSubject<MenuItem | null>(null);
  private menuItemDeletedSource = new BehaviorSubject<string | undefined | null>(null);

  // Observable stream to be consumed by components
  menuItemCreated$ = this.menuItemCreatedSource.asObservable();
  menuItemDeleted$ = this.menuItemDeletedSource.asObservable();
  menuItemUpdated$ = this.menuItemUpdatedSource.asObservable();

  constructor() {}
  // Emit event when a order is created
  menuItemCreated(menuItem: MenuItem): void {
    this.menuItemCreatedSource.next(menuItem);
  }

  // Emit event when a order is updated
  menuItemUpdated(menuItem: MenuItem): void {
    this.menuItemCreatedSource.next(menuItem);
  }

  // Emit event when a order is deleted
  menuItemDeleted(menuItemId: string | undefined | null): void {
    this.menuItemDeletedSource.next(menuItemId);
  }

  // Get all MenuItems
  getAllMenuItems(page: number = 1, limit: number = 10): Observable<MenuItem[]> {
    const offset = (page - 1) * limit;
    return from(
      databases.listDocuments(this.databaseId, this.MenuItemsCollectionId, [
        Query.limit(limit),
        Query.offset(offset),
        Query.orderDesc('$createdAt'),
      ]),
    ).pipe(
      map((result) => {
        if (!result) throw new Error('No result');
        return result.documents as unknown as MenuItem[];
      }),
      catchError((error) => {
        console.error('Error fetching MenuItems:', error);
        throw error;
      }),
    );
  }

  // Create an MenuItem
  createMenuItem(menuItemData: MenuItem): Observable<MenuItem> {
    return from(databases.createDocument(this.databaseId, this.MenuItemsCollectionId, ID.unique(), menuItemData)).pipe(
      map((result) => result as unknown as MenuItem),
      catchError((error) => {
        console.error('Error creating MenuItem:', error);
        throw error;
      }),
    );
  }

  // Get a single MenuItem by ID
  getMenuItemById(menuItemId: string): Observable<MenuItem> {
    return from(databases.getDocument(this.databaseId, this.MenuItemsCollectionId, menuItemId)).pipe(
      map((result) => result as unknown as MenuItem),
      catchError((error) => {
        console.error('Error fetching MenuItem:', error);
        throw error;
      }),
    );
  }

  // Update an MenuItem
  updateMenuItem(menuItemId: string, menuItemData: Partial<MenuItem>): Observable<MenuItem> {
    return from(databases.updateDocument(this.databaseId, this.MenuItemsCollectionId, menuItemId, menuItemData)).pipe(
      map((result) => result as unknown as MenuItem),
      catchError((error) => {
        console.error('Error updating MenuItem:', error);
        throw error;
      }),
    );
  }

  // Delete an MenuItem
  deleteMenuItem(menuItemId: string): Observable<void> {
    return from(databases.deleteDocument(this.databaseId, this.MenuItemsCollectionId, menuItemId)).pipe(
      map(() => undefined), // Converting to void
      catchError((error) => {
        console.error('Error deleting MenuItem:', error);
        throw error;
      }),
    );
  }

  getTopMenuItemsByOrderCount(): Observable<MenuItem[]> {
    return from(
      databases.listDocuments(this.databaseId, this.MenuItemsCollectionId,[ Query.limit(100)])
    ).pipe(
      map((result) => {
        if (!result || !result.documents) throw new Error('No result');
        const documents: MenuItem[] = result.documents as unknown as MenuItem[];
        // Sort the documents by the length of their 'orders' array in descending order
        const sortedDocuments = documents.sort((a, b) => (b.orders?.length || 0) - (a.orders?.length || 0));
        // Return the top 5 documents
        return sortedDocuments.slice(0, 5);
      }),
      catchError((error) => {
        console.error('Error fetching MenuItems:', error);
        throw error;
      }),
    );
  }
}
