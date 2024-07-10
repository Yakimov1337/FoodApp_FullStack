import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { User } from '../../../../../../core/models';
import { selectOrderToUpdate } from '../../../../../../core/state/modal/order/modal.selectors';
import { closeUpdateOrderModal } from '../../../../../../core/state/modal/order/modal.actions';
import { OrdersService } from '../../../../../../services/orders.service';
import { UserService } from '../../../../../../services/user.service';
import { CommonModule, formatDate } from '@angular/common';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: '[order-update-modal]',
  templateUrl: './order-update-modal.component.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
})
export class OrderUpdateModalComponent implements OnInit {
  orderForm: FormGroup;
  users$: Observable<User[]>;
  minDate: string;
  isMenuItemsDropdownOpen: boolean = false;
  orderedMenuItemTitles$: Observable<string[]> = of([]);

  private currentOrderId: string | null = null;

  constructor(
    private fb: FormBuilder,
    private store: Store,
    private ordersService: OrdersService,
    private userService: UserService,
    private toastr: ToastrService,
  ) {
    const currentDate = new Date().toISOString().split('T')[0];
    this.minDate = currentDate;
    this.orderForm = this.fb.group({
      user: ['', Validators.required],
      totalCost: ['', [Validators.required, Validators.pattern(/^-?(0|[1-9]\d*)?(\.\d+)?(?<=\d)$/)]],
      createdOn: ['', Validators.required],
      status: ['', Validators.required],
      paid: [false, Validators.required],
      menuItems: this.fb.array([]),
    });
    this.users$ = this.userService.getAllUsers();
  }

  ngOnInit(): void {
    this.store.select(selectOrderToUpdate).subscribe((order) => {
      if (order) {
        this.currentOrderId = order.$id;
        this.orderForm.patchValue({
          user: order.user?.$id,
          totalCost: order.totalCost,
          paid: order.paid,
          createdOn: formatDate(order.createdOn, 'yyyy-MM-dd', 'en-US'),
          status: order.status,
        });

        // Directly extract the titles from the order object
        this.orderedMenuItemTitles$ = of(order.menuItems.map((item) => item.title));
      }
    });
  }

  updateOrder(): void {
    if (this.orderForm.valid && this.currentOrderId) {
      const { menuItems, ...orderData } = this.orderForm.value; // DON'T INCLUDE MENU ITEMS ID'S IN THE UPDATE DATA

      // Convert 'createdOn' to ISO 8601 format (YYYY-MM-DD) (Appwrite problems...)
      formatDate(orderData.createdOn, 'yyyy-MM-dd', 'en-US')
      this.ordersService.updateOrder(this.currentOrderId, orderData).subscribe({
        next: (order) => {
          this.closeModal();
          this.ordersService.orderUpdated(order); //Notify about order update
          this.toastr.success('Order updated successfully!');
        },
        error: (error) => this.toastr.error('Error updating this order:', error),
      });
    } else {
      this.orderForm.markAllAsTouched();
      // If the form is invalid, iterate over the controls and log the errors
      Object.keys(this.orderForm.controls).forEach((key) => {
        const control = this.orderForm.get(key);
        const errors = control?.errors ?? {};
        Object.keys(errors).forEach((keyError) => {
          this.toastr.error(`Form Invalid - control: ${key}, Error: ${keyError}`);
        });
      });
    }
  }

  closeModal(): void {
    this.store.dispatch(closeUpdateOrderModal());
  }
}
