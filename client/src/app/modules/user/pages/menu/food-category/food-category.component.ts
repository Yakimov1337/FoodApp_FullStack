import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-food-category',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './food-category.component.html',
})
export class FoodCategoryComponent implements OnInit {
  selectedCategory = 'burger'; // Default category
  @Output() categorySelected = new EventEmitter<string>();
  categories = [
    { label: 'Pizza', value: 'Pizza', icon: 'assets/images/pizza.png' },
    { label: 'Burger', value: 'Burger', icon: 'assets/images/burger.png' },
    { label: 'Pasta', value: 'Pasta', icon: 'assets/images/pasta.png' },
    { label: 'Sushi', value: 'Sushi', icon: 'assets/images/sushi.png' },
    { label: 'Salad', value: 'Salad', icon: 'assets/images/salad.png' },
    { label: 'Tacos', value: 'Tacos', icon: 'assets/images/tacos.png' },
    { label: 'Dessert', value: 'Dessert', icon: 'assets/images/desert.png' },
    { label: 'Other', value: 'Other', icon: 'assets/images/other.png' },
  ];

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      // Update selectedCategory based on queryParams or default to 'Burger'
      this.selectedCategory = params['category'] || this.selectedCategory;
    });
  }

  selectCategory(category: { label: string; value: string }): void {
    // Navigate and update the queryParams upon selection
    this.router.navigate(['/menu'], { queryParams: { category: category.value.toLowerCase() } });
  }
}
