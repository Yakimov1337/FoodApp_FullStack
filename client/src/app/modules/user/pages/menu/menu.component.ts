import { CommonModule } from '@angular/common';
import { Component, ElementRef, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MenuItem } from '../../../../core/models';
import { MenuItemsService } from '../../../../services/menuItems.service';
import { FoodCardComponent } from './food-card/food-card.component';
import { FoodCategoryComponent } from './food-category/food-category.component';
import { LoaderComponent } from '../../../../shared/components/loader/loader.component';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, FoodCardComponent, FoodCategoryComponent, LoaderComponent],
  templateUrl: './menu.component.html',
})
export class MenuComponent implements OnInit, OnDestroy {
  @ViewChild('menuItemsContainer') menuItemsContainer?: ElementRef;
  allMenuItems: MenuItem[] = [];
  displayMenuItems: MenuItem[] = [];
  isLoading = true;
  selectedCategory: string = 'burger';
  lastCategory: string = 'burger';
  itemsPerPage = 6; // Number of items to load
  currentPage = 1;

  private queryParamsSubscription!: Subscription;

  constructor(private menuItemsService: MenuItemsService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    // Listen for any changes in the URL query parameters.
    this.route.queryParams.subscribe((params) => {
      // Check if the 'category' parameter from the URL has changed.
      // 'categoryChanged' is true if there's a new category in the URL different from the current one.
      const categoryChanged = params['category'] && this.selectedCategory !== params['category'];

      // If the category has changed or if not loaded any menu items yet,
      // need to update our menu items based on the new category.
      if (categoryChanged || !this.allMenuItems.length) {
        // Set the selected category to the new one from the URL.
        // If there's no category in the URL, keep the current category. (burger by default)
        this.selectedCategory = params['category'] || this.selectedCategory;
        this.fetchMenuItems();
      }
    });
  }

  fetchMenuItems(): void {
    if (!this.allMenuItems.length) {
      // Check if items have already been fetched
      this.isLoading = true;
      this.menuItemsService.getAllMenuItems(1, 100).subscribe({
        next: (items) => {
          this.allMenuItems = items; // Saving all items then only filter based on selected category
          // Filter immediately after fetching based on the selected category
          this.filterItemsByCategory();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Failed to fetch menu items', error);
          this.isLoading = false;
        },
      });
    } else {
      this.filterItemsByCategory(); // Filter existing items if they've already been fetched
    }
  }

  filterItemsByCategory(): void {
    // w/o this, while changing categories displayItems count will be same until page refresh
    if (this.lastCategory !== this.selectedCategory) {
      // Reset currentPage if the category has changed
      this.currentPage = 1;
      this.lastCategory = this.selectedCategory;
    }

    const filteredItems = this.allMenuItems.filter(
      (item) => item.category.toLowerCase() === this.selectedCategory.toLowerCase(),
    );

    // Only show items up to the current page
    //creates a new array rather than mutating it
    this.displayMenuItems = [...filteredItems.slice(0, this.currentPage * this.itemsPerPage)];
  }
  @HostListener('window:scroll', ['$event'])
  onWindowScroll(): void {
    // Calculate the distance from the top of the page to the bottom of the viewport
    const distanceFromTopToBottom = window.innerHeight + window.scrollY;
    // Calculate the threshold for triggering the event (95% of the document height)
    const scrollThreshold = document.body.offsetHeight * 0.95;

    // Check if we're at 95% of the bottom of the page
    if (distanceFromTopToBottom >= scrollThreshold) {
      this.loadMoreItems();
    }
  }

  loadMoreItems(): void {
    this.currentPage++;
    this.filterItemsByCategory();
  }

  ngOnDestroy(): void {
    // Unsubscribe to prevent memory leaks
    this.queryParamsSubscription?.unsubscribe();
  }
}
