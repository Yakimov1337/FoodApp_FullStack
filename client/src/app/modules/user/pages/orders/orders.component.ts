import { Component } from '@angular/core';
import { OrdersTableComponent } from './components/orders-table/orders-table.component';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [  OrdersTableComponent,],
  templateUrl: './orders.component.html',
})
export class OrdersComponent {

}
