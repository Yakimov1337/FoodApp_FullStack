import { Observable, Subscription, interval, startWith, switchMap } from 'rxjs';
import { Component, OnInit } from '@angular/core';
import { MenuItemAuctionsTableItemComponent } from '../menuItem-auctions-table-item/menuItem-auctions-table-item.component';
import { CommonModule, NgFor } from '@angular/common';
import { MenuItemsService } from '../../../../../../services/menuItems.service';
import { MenuItem } from '../../../../../../core/models';
import { LoaderComponent } from '../../../../../../shared/components/loader/loader.component';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { ButtonComponent } from '../../../../../../shared/components/button/button.component';
import { openCreateMenuItemModal } from '../../../../../../core/state/modal/menuItem/modal.actions';
import { Store } from '@ngrx/store';
import { PaginationComponent } from '../../../../../../shared/components/pagination/pagination.component';
import { ToastrService } from 'ngx-toastr';
import { FormsModule } from '@angular/forms';

@Component({
  selector: '[menuItem-auctions-table]',
  templateUrl: './menuItem-auctions-table.component.html',
  standalone: true,
  imports: [
    NgFor,
    AngularSvgIconModule,
    ButtonComponent,
    MenuItemAuctionsTableItemComponent,
    CommonModule,
    LoaderComponent,
    PaginationComponent,
    FormsModule
  ],
})
export class MenuItemAuctionsTableComponent implements OnInit {
  public menuItems: MenuItem[] = [];
  public isLoading: boolean = true;
  public currentPage = 1;
  public totalPages!: number;
  public timeSinceLastUpdate$!: Observable<number>;
  public lastUpdated: Date = new Date();
  public categoryFilter: string = '';
  public priceSortDirection: string = '';
  public defaultFilter: string = 'all';
  public originalMenuItems: MenuItem[] = [];

  private subscriptions: Subscription = new Subscription();

  constructor(private menuItemsService: MenuItemsService, private store: Store, private toastr: ToastrService) {}

  ngOnInit(): void {
    this.loadMenuItems(this.currentPage);
    this.initializeSubscriptions();
    this.initializeTimeSinceLastUpdate();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  loadMenuItems(page: number, limit: number = 10): void {
    this.isLoading = true;
    this.menuItemsService.getAllMenuItems(page, limit).subscribe({
      next: (menuItems) => {
        this.originalMenuItems = menuItems;
        this.menuItems = [...this.originalMenuItems]; // Copy for display purposes
        this.totalPages = Math.ceil(100 / limit); //change later
        this.isLoading = false;
      },
      error: (error) => {
        this.toastr.error('Error fetching items:', error);
        this.isLoading = false;
      },
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadMenuItems(this.currentPage);
  }

  openCreateModal() {
    console.log('Dispatching openCreateOrderModal action');
    this.store.dispatch(openCreateMenuItemModal());
  }

  private initializeTimeSinceLastUpdate(): void {
    this.timeSinceLastUpdate$ = interval(60000) // Emit value every 60 seconds
      .pipe(
        startWith(0), // Start immediately upon subscription
        switchMap(() => {
          const now = new Date();
          const difference = now.getTime() - this.lastUpdated.getTime();
          return [Math.floor(difference / 60000)]; // Convert to minutes
        }),
      );
  }

  private initializeSubscriptions(): void {
    const menuItemsCreatedSub = this.menuItemsService.menuItemCreated$.subscribe((menuItem) => {
      if (menuItem) {
        this.loadMenuItems(this.currentPage); // Refetch items after a new item is created
      }
    });
    const menuItemsDeletedSub = this.menuItemsService.menuItemDeleted$.subscribe((deleteMenuItemId) => {
      if (deleteMenuItemId) {
        this.menuItems = this.menuItems.filter((menuItem) => menuItem.$id !== deleteMenuItemId);
      }
    });
    this.subscriptions.add(menuItemsCreatedSub);
    this.subscriptions.add(menuItemsDeletedSub);
  }

  applyFiltersAndSorting(): void {
    let filteredItems = [...this.originalMenuItems]; // Start with a copy of the original

    if (this.categoryFilter && this.categoryFilter !== 'All') {
      filteredItems = filteredItems.filter(item => item.category === this.categoryFilter);
    }

    if (this.defaultFilter !== 'all') {
      const isDefault = this.defaultFilter === 'yes';
      filteredItems = filteredItems.filter(item => item.default === isDefault);
    }

    if (this.priceSortDirection) {
      filteredItems.sort((a, b) => this.priceSortDirection === 'asc' ? a.price - b.price : b.price - a.price);
    }

    this.menuItems = filteredItems; // Update menuItems for display
  }
}
