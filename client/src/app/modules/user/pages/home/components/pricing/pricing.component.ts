import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { ToastrService } from 'ngx-toastr';
import { StripeService } from '../../../../../../services/stripe.service';

@Component({
  selector: 'app-pricing',
  standalone: true,
  imports: [AngularSvgIconModule, CommonModule],
  templateUrl: './pricing.component.html',
})
export class PricingComponent {
  plans = [
    {
      name: 'Basic Bite',
      id: 'price_1P0kaMHTUTLeEgjn0T3mo9fx',
      description: 'Ideal for small restaurants or startups looking to expand their delivery options.',
      price: '$19',
      frequency: '/mo',
      features: ['Up to 50 delivery orders', 'Basic analytics', 'Community support'],
      actionText: 'Get Started',
      actionLink: '#',
    },
    {
      name: 'Gourmet Growth',
      id: 'price_1P0kJCHTUTLeEgjnRilGp9Db',
      description: 'Perfect for growing businesses aiming to increase their market presence.',
      price: '$39',
      frequency: '/mo',
      features: ['Unlimited delivery orders', 'Advanced analytics', 'Priority support'],
      actionText: 'Get Started',
      actionLink: '#',
    },
    {
      name: 'Epicurean Enterprise',
      id: 'price_1P0kb5HTUTLeEgjn4zAC25My',
      description: 'Large-scale solutions for established restaurants with custom needs.',
      price: 'Custom',
      features: ['Custom order volume', 'Personalized menu consulting', 'Dedicated account manager'],
      actionText: 'Contact Sales',
      actionLink: '#',
    },
  ];

  constructor(private stripeService: StripeService, private toastr: ToastrService) {}

  subscribe(planId: string) {
    this.stripeService.checkoutSubscription(planId);
  }
}
