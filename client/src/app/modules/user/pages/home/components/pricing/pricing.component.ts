import { CommonModule } from '@angular/common';
import {
  AfterViewInit,
  Component,
  ElementRef,
  HostListener,
  OnInit,
  QueryList,
  Renderer2,
  ViewChildren,
} from '@angular/core';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { ToastrService } from 'ngx-toastr';
import { StripeService } from '../../../../../../services/stripe.service';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-pricing',
  standalone: true,
  imports: [AngularSvgIconModule, CommonModule],
  templateUrl: './pricing.component.html',
  animations: [
    trigger('enterFromLeft', [
      transition('* => true', [
        style({ opacity: 0, transform: 'translateX(-100%)' }),
        animate('1s', style({ opacity: 1, transform: 'translateX(0)' })),
      ]),
    ]),
    trigger('enterFromRight', [
      transition('* => true', [
        style({ opacity: 0, transform: 'translateX(100%)' }),
        animate('1s', style({ opacity: 1, transform: 'translateX(0)' })),
      ]),
    ]),
  ],
})
export class PricingComponent implements AfterViewInit {
  @ViewChildren('planElement') planElements!: QueryList<ElementRef>;
  isVisible: boolean[] = [false, true, false];
  constructor(private stripeService: StripeService, private renderer: Renderer2) {}

  ngAfterViewInit(): void {
    this.planElements.forEach((plan, index) => {
      if (index === 0 || index === 2) {
        // Only observe the 1st and 3rd plans
        const observer = new IntersectionObserver(
          (entries) => {
            entries.forEach((entry) => {
              if (entry.isIntersecting) {
                this.isVisible[index] = true; // Set the flag to true when in view
                observer.unobserve(entry.target); // Stop observing once visible
              }
            });
          },
          { threshold: 0.5 },
        );

        observer.observe(plan.nativeElement);
      }
    });
  }

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

  subscribe(planId: string) {
    this.stripeService.checkoutSubscription(planId);
  }
}
