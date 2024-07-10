import { Injectable } from '@angular/core';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Query } from 'appwrite';
import { environment } from 'src/environments/environment.local';
import { databases, ID } from '../core/lib/appwrite';
import { Order, OrderSubmission } from '../core/models';

@Injectable({
  providedIn: 'root',
})
export class OrdersService {
  private readonly databaseId = environment.appwriteDatabaseId;
  private readonly ordersCollectionId = environment.ordersCollectionId;
  private orderCreatedSource = new BehaviorSubject<Order | null>(null);
  private orderUpdatedSource = new BehaviorSubject<Order | null>(null);
  private orderDeletedSource = new BehaviorSubject<string | undefined | null>(null);

  // Observable stream to be consumed by components
  orderCreated$ = this.orderCreatedSource.asObservable();
  orderDeleted$ = this.orderDeletedSource.asObservable();
  orderUpdated$ = this.orderUpdatedSource.asObservable();

  constructor() {}
  // Emit event when a order is created
  orderCreated(order: Order): void {
    this.orderCreatedSource.next(order);
  }

  // Emit event when a order is updated
  orderUpdated(order: Order): void {
    this.orderCreatedSource.next(order);
  }

  // Emit event when a order is deleted
  orderDeleted(orderId: string | undefined | null): void {
    this.orderDeletedSource.next(orderId);
  }

  // Create an Order
  createOrder(orderData: OrderSubmission): Observable<Order> {
    return from(databases.createDocument(this.databaseId, this.ordersCollectionId, ID.unique(), orderData)).pipe(
      map((result) => result as unknown as Order),
      catchError((error) => {
        console.error('Error creating order:', error);
        throw error;
      }),
    );
  }

  // Get all Orders
  getAllOrders(page: number = 1, limit: number = 10): Observable<Order[]> {
    const offset = (page - 1) * limit;
    return from(
      databases.listDocuments(this.databaseId, this.ordersCollectionId, [
        Query.limit(limit),
        Query.offset(offset),
        Query.orderDesc('$createdAt'),
      ]),
    ).pipe(
      map((result) => {
        if (!result) throw new Error('No result');
        return result.documents as unknown as Order[];
      }),
      catchError((error) => {
        console.error('Error fetching orders:', error);
        throw error;
      }),
    );
  }

  // Get a single Order by ID
  getOrderById(orderId: string): Observable<Order> {
    return from(databases.getDocument(this.databaseId, this.ordersCollectionId, orderId)).pipe(
      map((result) => result as unknown as Order),
      catchError((error) => {
        console.error('Error fetching order:', error);
        throw error;
      }),
    );
  }

  // Update an Order
  updateOrder(orderId: string, orderData: Partial<Order>): Observable<Order> {
    return from(databases.updateDocument(this.databaseId, this.ordersCollectionId, orderId, orderData)).pipe(
      map((result) => result as unknown as Order),
      catchError((error) => {
        console.error('Error updating order:', error);
        throw error;
      }),
    );
  }

  // Delete an Order
  deleteOrder(orderId: string): Observable<void> {
    return from(databases.deleteDocument(this.databaseId, this.ordersCollectionId, orderId)).pipe(
      map(() => undefined), // Mapping to void
      catchError((error) => {
        console.error('Error deleting order:', error);
        throw error;
      }),
    );
  }

  // Fetch orders for a specific user
  getOrdersByUserId(userId: string, page: number = 1, limit: number = 10): Observable<Order[]> {
    const offset = (page - 1) * limit;
    return from(
      databases.listDocuments(this.databaseId, this.ordersCollectionId, [
        Query.limit(limit),
        Query.offset(offset),
        Query.orderDesc('$createdAt'),
        Query.equal('user', userId),
      ]),
    ).pipe(
      map((result) => {
        if (!result) throw new Error('No result');
        return result.documents as unknown as Order[];
      }),
      catchError((error) => {
        console.error('Error fetching orders for user:', error);
        throw error;
      }),
    );
  }
}
