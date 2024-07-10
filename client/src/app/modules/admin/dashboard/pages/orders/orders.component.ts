import { Store } from '@ngrx/store';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrdersTableComponent } from '../../components/orders/orders-table/orders-table.component';
import { selectIsCreateOrderModalOpen, selectIsDeleteOrderModalOpen, selectIsUpdateOrderModalOpen } from '../../../../../core/state/modal/order/modal.selectors';
import { OrderUpdateModalComponent } from '../../components/orders/order-update-modal/order-update-modal.component';
import { OrderCreateModalComponent } from '../../components/orders/order-create-modal/order-create-modal.component';
import { OrderDeleteModalComponent } from '../../components/orders/order-delete-modal/order-delete-modal.component';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [
    CommonModule,
    OrdersTableComponent,
    OrderUpdateModalComponent,
    OrderCreateModalComponent,
    OrderDeleteModalComponent,
  ],
  templateUrl: './orders.component.html',
})
export class OrdersComponent {
  showUpdateOrderModal$ = this.store.select(selectIsUpdateOrderModalOpen);
  showCreateOrderModal$ = this.store.select(selectIsCreateOrderModalOpen);
  showDeleteOrderModal$ = this.store.select(selectIsDeleteOrderModalOpen);


  constructor(private store: Store) {}
}
