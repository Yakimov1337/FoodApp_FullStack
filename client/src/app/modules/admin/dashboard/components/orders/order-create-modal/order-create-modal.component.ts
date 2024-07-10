import { Observable, of } from 'rxjs';
import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { CommonModule, formatDate } from '@angular/common';
import { MenuItem, Order, User } from '../../../../../../core/models';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators, FormControl, FormArray, ValidatorFn, AbstractControl } from '@angular/forms';
import { OrdersService } from '../../../../../../services/orders.service';
import { closeCreateOrderModal } from '../../../../../../core/state/modal/order/modal.actions';
import { UserService } from '../../../../../../services/user.service';
import { MenuItemsService } from '../../../../../../services/menuItems.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: '[order-create-modal]',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './order-create-modal.component.html',
})
export class OrderCreateModalComponent {
  orderForm: FormGroup;
  users$: Observable<User[]> = of([]); //initial value
  menuItems$!: Observable<MenuItem[]>;
  minDate: string;
  isMenuItemsDropdownOpen: boolean = false;

  constructor(
    private fb: FormBuilder,
    private orderService: OrdersService,
    private userService: UserService,
    private menuItemsService: MenuItemsService,
    private store: Store,
    private toastr: ToastrService,
  ) {

    const currentDate = new Date().toISOString().split('T')[0];
    this.minDate = currentDate;
    this.orderForm = this.fb.group({
      user: ['', Validators.required],
      totalCost: [1, [Validators.required, Validators.pattern(/^-?(0|[1-9]\d*)?(\.\d+)?(?<=\d)$/)]],
      createdOn: [currentDate, [Validators.required]],
      menuItems: this.fb.array([]),
      paid: [false, Validators.required],
      status: ['pending', Validators.required],
    });
  }
  ngOnInit() {
    this.users$ = this.userService.getAllUsers();
    this.menuItems$ = this.menuItemsService.getAllMenuItems(1, 100);
  }

  // Function to handle form submission
  createOrder(): void {
    if (this.orderForm.valid) {
      const orderData = this.orderForm.value;

      //Set selected items id's
      orderData.menuItems = (this.orderForm.get('menuItems') as FormArray).value.map((itemId: string) => itemId.toString());
      // Convert 'createdOn' to ISO 8601 format (YYYY-MM-DD) (Appwrite problems...)
      formatDate(orderData.createdOn, 'yyyy-MM-dd', 'en-US')
      this.orderService.createOrder(orderData).subscribe({
        next: (order: Order) => {
          this.closeModal();
          this.resetForm(); // Reset form to default state after creation
          this.orderService.orderCreated(order); // Notify about the new Order !!! VERY IMPORTANT- REFETCH THE TABLE
          this.toastr.success('Order created successfully!');
        },
        error: (error: any) => this.toastr.error('Failed to create new order!', error),
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

  onMenuItemSelected(event: Event, menuItemId: string): void {
    const inputElement = event.target as HTMLInputElement;
    const menuItemsFormArray = this.orderForm.get('menuItems') as FormArray;

    if (inputElement.checked) {
      menuItemsFormArray.push(new FormControl(menuItemId));
    } else {
      const index = menuItemsFormArray.controls.findIndex(control => control.value === menuItemId);
      if (index !== -1) {
        menuItemsFormArray.removeAt(index);
      }
    }
  }

  closeModal(): void {
    this.store.dispatch(closeCreateOrderModal());
  }

  resetForm(): void {
    const currentDate = new Date().toISOString().split('T')[0];
    this.orderForm.reset({
      user: '',
      menuItems: new FormArray([]),
      totalCost: '1',
      createdOn: currentDate,
      status: 'pending',
    });
  }

  dateFormatValidator(): ValidatorFn {
    return (control: AbstractControl): {[key: string]: any} | null => {
      if (!control.value) return null; // don't validate empty value
      const regex = /^\d{2}\/\d{2}\/\d{2}$/;
      return regex.test(control.value) ? null : {'dateFormat': {value: control.value}};
    };
  }
}
