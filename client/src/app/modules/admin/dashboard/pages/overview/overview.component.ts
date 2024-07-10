import { Component, OnInit } from '@angular/core';
import { MenuItemOverviewTableComponent } from '../../components/overview/menuItem-overview-table/menuItem-overview-table.component';
import { MenuItemChartCardComponent } from '../../components/overview/menuItem-chart-card/menuItem-chart-card.component';
import { MenuItemSingleCardComponent } from '../../components/overview/menuItem-single-card/menuItem-single-card.component';
import { MenuItemDualCardComponent } from '../../components/overview/menuItem-dual-card/menuItem-dual-card.component';
import { MenuItemHeaderComponent } from '../../components/overview/menuItem-header/menuItem-header.component';
import { MenuItemsService } from '../../../../../services/menuItems.service';
import { MenuItem } from '../../../../../core/models';
import { Subscription } from 'rxjs';
import { LoaderComponent } from '../../../../../shared/components/loader/loader.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-MenuItem',
  templateUrl: './overview.component.html',
  standalone: true,
  imports: [
    MenuItemHeaderComponent,
    MenuItemDualCardComponent,
    MenuItemSingleCardComponent,
    MenuItemChartCardComponent,
    MenuItemOverviewTableComponent,
    LoaderComponent,
    CommonModule,
  ],
})
export class OverviewComponent implements OnInit {
  public menuItems: MenuItem[] = [];
  public isLoading: boolean = true;
  public currentPage = 1;
  public totalPages!: number;

  private subscriptions: Subscription = new Subscription();

  constructor(private menuItemsService: MenuItemsService) {}

  ngOnInit(): void {
    this.loadMenuItems(this.currentPage);
  }
  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
  loadMenuItems(page: number, limit: number = 5): void {
    this.isLoading = true;
    this.menuItemsService.getTopMenuItemsByOrderCount().subscribe({
      next: (menuItems) => {
        this.menuItems = menuItems;
        this.totalPages = Math.ceil(50 / limit); //change later
        this.isLoading = false;
      },
      error: (error) => {
        console.log('Error fetching items:', error);
        this.isLoading = false;
      },
    });
  }
}
