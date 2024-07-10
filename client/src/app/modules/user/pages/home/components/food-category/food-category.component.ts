import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-food-category',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './food-category.component.html',
})
export class FoodCategoryComponent {
  constructor(private router: Router) {}
  navigateToMenuWithCategory(category: string): void {
    this.router.navigate(['/menu'], { queryParams: { category: category.toLowerCase() } });
  }

  foods = [
    {
      name: 'Burgers',
      description: 'Beef patties, lettuce, tomatoes, sauces, sesame buns.',
      imageUrl: 'assets/images/burger-home.jpg',
      category: 'Burger',
    },
    {
      name: 'Pizza',
      description: 'Dough, tomato sauce, mozzarella, various toppings.',
      imageUrl: 'assets/images/pizza.jpg',
      category: 'Pizza',
    },
    {
      name: 'Pasta',
      description: 'Al dente pasta, creamy or tomato sauces, meats, veggies.',
      imageUrl: 'assets/images/pasta.jpg',
      category: 'Pasta',
    },
    {
      name: 'Sushi',
      description: 'Sliced fish, seasoned rice, seafood and veggie rolls.',
      imageUrl: 'assets/images/sushi.jpg',
      category: 'Sushi',
    },
    {
      name: 'Salad',
      description: 'Greens, veggies, nuts, fruits, dressings.',
      imageUrl: 'assets/images/salad.jpg',
      category: 'Salad',
    },
    {
      name: 'Tacos',
      description: 'Tortillas with meats, salsa, guacamole, cheese, lime.',
      imageUrl: 'assets/images/tacos.jpg',
      category: 'Tacos',
    },
    {
      name: 'Dessert',
      description: 'Cheesecakes, chocolates, sweet treats.',
      imageUrl: 'assets/images/dessert.jpg',
      category: 'Dessert',
    },
    {
      name: 'Other',
      description: 'Global cuisines, unique dishes, varied tastes.',
      imageUrl: 'assets/images/other.jpg',
      category: 'Other',
    },
  ];
}
